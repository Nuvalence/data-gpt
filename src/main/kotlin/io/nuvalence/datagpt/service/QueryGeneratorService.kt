package io.nuvalence.datagpt.service

import com.theokanning.openai.completion.CompletionRequest
import io.nuvalence.datagpt.client.LlmClient
import io.nuvalence.datagpt.domain.Query
import io.nuvalence.datagpt.domain.Table
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class QueryGeneratorService(private val llm: LlmClient, private val tableIntrospectionService: TableIntrospectionService) {

    companion object {
        private val log = LoggerFactory.getLogger(QueryGeneratorService::class.java)
    }

    fun generateQuery(question: String): List<Query> {
        val request = createCompletionRequestForQuestion(question)
        return llm.sendCompletionRequest(request).choices
            .map { it.text }
            .map { sql ->
                val doubleCheckRequest = createCompletionRequestForQueryCheck(sql)
                val checkedSql = llm.sendCompletionRequest(doubleCheckRequest).choices.first().text
                if (sql == checkedSql) {
                    Query(sql)
                } else {
                    log.info("Original query was sanitized by OpenAI: {} -> {}", sql, checkedSql)
                    Query(checkedSql)
                }
            }
    }

    private fun schemaSummaryForTable(table: Table): String {
        return """
Schema for table: ${table.name}
${table.columns.joinToString("\n") { "  ${it.name} ${it.type}" }}
        """
    }

    private fun createCompletionRequestForQuestion(question: String): CompletionRequest {
        val tableSummaries = tableIntrospectionService.findAllTables()
            .joinToString("\n\n") { schemaSummaryForTable(it) }
        val prompt = """
$tableSummaries

Given the above schemas, write a detailed and correct Postgres sql query to answer the analytical question:

"$question"

The query should prefer names over ids. Return only the query.
        """.trimIndent()
        return CompletionRequest.builder()
            .prompt(prompt)
            .model("text-davinci-003")
            .maxTokens(500)
            .temperature(0.75)
            .n(3)
            .build()
    }

    private fun createCompletionRequestForQueryCheck(sql: String): CompletionRequest {
        val prompt = """$sql
            

Double check the Postgres query above for common mistakes, including:
 - Handling case sensitivity, e.g. using ILIKE instead of LIKE
 - Ensuring the join columns are correct
 - Casting values to the appropriate type
 - Properly quoting identifiers
 
Rewrite the query below if there are any mistakes. If it looks good as it is, just return the original query.

Return only the query.
        """.trimIndent()
        return CompletionRequest.builder()
            .prompt(prompt)
            .model("text-davinci-003")
            .maxTokens(500)
            .temperature(0.8)
            .build()
    }

}
