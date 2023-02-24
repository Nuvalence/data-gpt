package io.nuvalence.datagpt.config

import com.theokanning.openai.service.OpenAiService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAiConfig {

    @Bean
    fun openAiService(@Value("\${openai.api-key}") apiKey: String): OpenAiService {
        return OpenAiService(apiKey)
    }

}
