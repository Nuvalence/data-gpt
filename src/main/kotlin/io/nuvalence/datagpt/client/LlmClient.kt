package io.nuvalence.datagpt.client

import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.completion.CompletionResult
import com.theokanning.openai.service.OpenAiService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class LlmClient(@Value("\${openai.api-key}") private val apiKey: String) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(LlmClient::class.java)
    }

    fun sendCompletionRequest(request: CompletionRequest): CompletionResult {
        log.info("Sending completion request to OpenAI: $request")
        val llm = OpenAiService(apiKey)
        return llm.createCompletion(request)
    }

}
