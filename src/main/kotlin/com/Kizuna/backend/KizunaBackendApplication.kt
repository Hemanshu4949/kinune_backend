package com.kizuna.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.kizuna", "com.kizuna"])
class KizunaBackendApplication

fun main(args: Array<String>) {
    runApplication<KizunaBackendApplication>(*args)
}
