package io.nuvalence.datagpt.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.theokanning.openai.completion.CompletionRequest
import io.nuvalence.datagpt.client.LlmClient
import io.nuvalence.datagpt.config.OpenAiProperties
import io.nuvalence.datagpt.domain.Answer
import io.nuvalence.datagpt.domain.Query
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class QuestionAnswerService(
    private val queryGeneratorService: QueryGeneratorService,
    private val llm: LlmClient,
    private val openAiProperties: OpenAiProperties,
    private val jdbcTemplate: JdbcTemplate,
    private val mapper: ObjectMapper) {

    companion object {
        private val log = LoggerFactory.getLogger(QuestionAnswerService::class.java)
    }

    fun answerQuestion(question: String): Answer {
        val queries = queryGeneratorService.generateQuery(question)
        queries.forEach { fetchQueryResult(it) }
        queries
            .filter { it.error != null || (it.result != null && it.result!!.isEmpty()) }
            .forEach { fixQuery(it) }
        val bestQuery = findBestQuery(queries)
        val bestQueryExplanation = explainQuery(bestQuery, question)
        return if (bestQuery.result != null) {
            if (bestQuery.result!!.size > 1) {
                Answer(question, "Here are the results I found.", mapper.valueToTree(bestQuery.result), bestQuery.sql, bestQueryExplanation)
            } else if (bestQuery.result!!.isEmpty()) {
                Answer(question, "I couldn't find any results.", mapper.valueToTree(bestQuery.result), bestQuery.sql, bestQueryExplanation)
            }
            else {
                val summary = summarizeResult(question, bestQuery.result!!)
                Answer(question, summary, mapper.valueToTree(bestQuery.result), bestQuery.sql, bestQueryExplanation)
            }
        } else {
            Answer(question, bestQuery = bestQuery.sql, error = bestQuery.error, bestQueryExplanation = bestQueryExplanation)
        }
    }

    private fun findBestQuery(queries: List<Query>): Query {
        for (q1 in queries) {
            for (q2 in queries) {
                if (q1.result != null && q1.result!!.isNotEmpty() && q1.result!!.size == q2.result?.size) {
                    return q1
                }
            }
        }
        for (query in queries) {
            if (query.result != null && query.result!!.isNotEmpty()) {
                return query
            }
        }
        return queries[0]
    }

    private fun fetchQueryResult(query: Query) {
        query.result = try {
            log.info("Executing query: {}", query.sql)
            jdbcTemplate.queryForList(query.sql)
        } catch (e: Exception) {
            log.error("Error executing query: {}", query.sql, e)
            query.error = ExceptionUtils.getRootCauseMessage(e)
            null
        }
    }

    private fun fixQuery(query: Query) {
        log.info("Fixing query {}", query)
        val prompt = if (query.error != null) {
            """
                ${query.sql}
                
                The Postgres sql query above produced the following error:
                
                ${query.error}
                
                Rewrite the Postgres sql query with the error fixed. Return only the query.
            """.trimIndent()
        } else {
            """
                ${query.sql}
                
                The Postgres query above produced no result. Try rewriting the query so it returns a result.
                
                Return only the query.
            """.trimIndent()
        }
        val sql = llm.sendCompletionRequest(CompletionRequest.builder()
            .prompt(prompt)
            .model(openAiProperties.model)
            .maxTokens(500)
            .temperature(0.8)
            .build()).choices.first().text
        log.info("Fixed query: {} -> {}", query.sql, sql)
        query.sql = sql
        query.error = null
        fetchQueryResult(query)
    }

    private fun summarizeResult(question: String, result: Any): String {
        log.info("Summarizing result: {}", result)
        val prompt = """
            ${mapper.writeValueAsString(result)}
            
            Given the above result, provide a direct answer to the following question with any related details:
            
            $question
        """.trimIndent()

        return llm.sendCompletionRequest(CompletionRequest.builder()
            .prompt(prompt)
            .model(openAiProperties.model)
            .maxTokens(500)
            .temperature(0.8)
            .build()).choices.first().text.trim()
    }

    private fun explainQuery(query: Query, question: String): String {
        log.info("Explaining query {}", query)
        val prompt = """
            ${query.sql}
            
            The above Postgres SQL query was used to answer the following question:
            
            "$question"
            
            As a senior data analyst, explain the Postgres SQL query above.
        """.trimIndent()
        return llm.sendCompletionRequest(CompletionRequest.builder()
            .prompt(prompt)
            .model(openAiProperties.model)
            .maxTokens(500)
            .temperature(0.8)
            .build()).choices.first().text
    }

}
