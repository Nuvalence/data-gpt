package io.nuvalence.datagpt.domain

data class AnswerRequest(val question: String, val persona: String? = null)
