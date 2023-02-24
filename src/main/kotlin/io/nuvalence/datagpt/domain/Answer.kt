package io.nuvalence.datagpt.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode

data class Answer(
    val question: String,
    val answer: String? = null,
    val result: JsonNode? = null,
    val bestQuery: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val error: String? = null
)
