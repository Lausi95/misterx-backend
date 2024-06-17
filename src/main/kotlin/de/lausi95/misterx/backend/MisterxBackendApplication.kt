package de.lausi95.misterx.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MisterxBackendApplication

fun main(args: Array<String>) {
    runApplication<MisterxBackendApplication>(*args)
}
