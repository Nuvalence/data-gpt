package io.nuvalence.datagpt.service

import com.theokanning.openai.completion.CompletionRequest
import io.nuvalence.datagpt.client.LlmClient
import io.nuvalence.datagpt.config.OpenAiProperties
import io.nuvalence.datagpt.domain.Query
import io.nuvalence.datagpt.domain.Table
import org.springframework.stereotype.Service

@Service
class QueryGeneratorService(
    private val llm: LlmClient,
    private val tableIntrospectionService: TableIntrospectionService,
    private val openAiProperties: OpenAiProperties) {

    fun generateQuery(question: String): List<Query> {
        val request = createCompletionRequestForQuestion(question)
        return llm.sendCompletionRequest(request).choices
            .map { Query(it.text) }
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

The query should prefer names over ids. Return only the query. Avoid common Postgres query mistakes, including:
 - Handling case sensitivity, e.g. using ILIKE instead of LIKE
 - Ensuring the join columns are correct
 - Casting values to the appropriate type
 - Properly quoting identifiers
 - Coalescing null values
        """.trimIndent()
        return CompletionRequest.builder()
            .prompt(prompt)
            .model(openAiProperties.model)
            .maxTokens(500)
            .temperature(0.75)
            .n(3)
            .build()
    }

}
