package io.nuvalence.datagpt.domain

data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val choices: List<ChatChoice>
)
