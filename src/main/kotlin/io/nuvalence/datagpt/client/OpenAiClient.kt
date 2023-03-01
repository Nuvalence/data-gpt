package io.nuvalence.datagpt.client

import io.nuvalence.datagpt.config.OpenAiProperties
import io.nuvalence.datagpt.domain.ChatCompletionRequest
import io.nuvalence.datagpt.domain.ChatCompletionResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Component
@EnableConfigurationProperties(OpenAiProperties::class)
class OpenAiClient(restTemplateBuilder: RestTemplateBuilder, openAiProperties: OpenAiProperties) {

    companion object {
        private val log = LoggerFactory.getLogger(OpenAiClient::class.java)
    }

    private val restTemplate: RestTemplate = restTemplateBuilder
        .rootUri(openAiProperties.baseUrl)
        .setReadTimeout(Duration.ofSeconds(30))
        .defaultHeader("Authorization", "Bearer ${openAiProperties.apiKey}")
        .build()

    fun sendChatCompletionRequest(request: ChatCompletionRequest): ChatCompletionResponse {
        log.info("Sending chat completion request to OpenAI: {}", request)
        return restTemplate.postForObject("/chat/completions", request, ChatCompletionResponse::class.java)!!
    }

}
