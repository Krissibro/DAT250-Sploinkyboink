package com.example.sploinkyboink

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class SploinkyboinkApplication

fun main(args: Array<String>) {
	runApplication<SploinkyboinkApplication>(*args)
}
