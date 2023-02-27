package io.nuvalence.datagpt.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "openai")
@ConstructorBinding
data class OpenAiProperties(val apiKey: String, val model: String)
