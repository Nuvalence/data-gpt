package io.nuvalence.datagpt.domain

data class Query(
    var sql: String,
    var result: List<Map<String, Any?>>? = null,
    var error: String? = null
)
