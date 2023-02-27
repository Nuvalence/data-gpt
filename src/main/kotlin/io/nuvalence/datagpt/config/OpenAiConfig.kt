package io.nuvalence.datagpt.config

import com.theokanning.openai.service.OpenAiService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableConfigurationProperties(OpenAiProperties::class)
class OpenAiConfig {

    @Bean
    fun openAiService(properties: OpenAiProperties): OpenAiService {
        return OpenAiService(properties.apiKey, Duration.ZERO)
    }

}
