package io.nuvalence.datagpt.domain

data class Table(val name: String, val columns: List<Column>, val sampleData: List<List<Any?>>)
