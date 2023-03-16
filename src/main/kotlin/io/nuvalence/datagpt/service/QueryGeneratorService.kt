package io.nuvalence.datagpt.service

import io.nuvalence.datagpt.client.OpenAiClient
import io.nuvalence.datagpt.config.OpenAiProperties
import io.nuvalence.datagpt.domain.ChatCompletionRequest
import io.nuvalence.datagpt.domain.ChatMessage
import io.nuvalence.datagpt.domain.Query
import io.nuvalence.datagpt.domain.Table
import org.springframework.stereotype.Service

@Service
class QueryGeneratorService(
    private val openAiClient: OpenAiClient,
    private val tableIntrospectionService: TableIntrospectionService,
    private val openAiProperties: OpenAiProperties
) {

    fun generateQuery(question: String): List<Query> {
        val request = createCompletionRequestForQuestion(question)
        return openAiClient.sendChatCompletionRequest(request).choices
            .map { Query(it.message.content) }
    }

    private fun schemaSummaryForTable(table: Table): String {
        return """
Schema for table: ${table.name}
${table.columns.joinToString("\n") { "  ${it.name} ${it.type} ${it.foreignKey ?: ""}" }}
        """
    }

    private fun createCompletionRequestForQuestion(question: String): ChatCompletionRequest {
        val tableSummaries = tableIntrospectionService.findAllTables()
            .joinToString("\n\n") { schemaSummaryForTable(it) }
        val prompt = """
$tableSummaries

Given the above schemas, write a detailed and correct Postgres sql query to answer the analytical question:

"$question"

The query should prefer names over ids. Return only the query. Do not include an explanation. Avoid common Postgres query mistakes, including:
 - Prefer ILIKE over exact matches
 - Prefer NOT ILIKE over negated exact matches
 - Handling case sensitivity, e.g. using ILIKE instead of LIKE
 - Ensuring the join columns are correct
 - Casting values to the appropriate type
 - Properly quoting identifiers
 - Coalescing null values
        """.trimIndent()

        return ChatCompletionRequest(
            model = openAiProperties.model,
            messages = listOf(ChatMessage("user", prompt)),
            maxTokens = 500,
            temperature = 0.75,
            n = 3
        )
    }

}
