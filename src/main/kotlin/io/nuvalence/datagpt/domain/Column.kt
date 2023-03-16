package io.nuvalence.datagpt.domain

data class Column(val name: String, val type: String, val foreignKey: String? = null)
