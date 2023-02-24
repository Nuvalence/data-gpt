package io.nuvalence.datagpt.client

import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.completion.CompletionResult
import com.theokanning.openai.service.OpenAiService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LlmClient(private val openAiService: OpenAiService) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(LlmClient::class.java)
    }

    fun sendCompletionRequest(request: CompletionRequest): CompletionResult {
        log.info("Sending completion request to OpenAI: $request")
        return openAiService.createCompletion(request)
    }

}
