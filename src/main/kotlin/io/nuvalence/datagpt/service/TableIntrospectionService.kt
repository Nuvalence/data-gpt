package io.nuvalence.datagpt.service

import io.nuvalence.datagpt.domain.Column
import io.nuvalence.datagpt.domain.Table
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class TableIntrospectionService(
    private val jdbcTemplate: JdbcTemplate,
    @Value("\${tables}") private val tables: List<String>) {

    fun findAllTables(): List<Table> {
        return tables.map { table ->
            val columns = jdbcTemplate.queryForList("""
                SELECT column_name, data_type
                FROM information_schema.columns
                WHERE table_schema = 'public'
                    AND table_name = ?
                    AND data_type not in ('ARRAY', 'tsvector')
                ORDER BY table_name, ordinal_position;
            """, table).map { row ->
                Column(row["column_name"] as String, row["data_type"] as String)
            }
            Table(table, columns)
        }
    }

}
