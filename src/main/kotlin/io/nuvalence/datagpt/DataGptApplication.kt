package io.nuvalence.datagpt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DataGptApplication

fun main(args: Array<String>) {
    runApplication<DataGptApplication>(*args)
}
