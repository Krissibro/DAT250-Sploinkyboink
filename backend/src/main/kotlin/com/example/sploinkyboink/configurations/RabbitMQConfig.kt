package com.example.sploinkyboink.configurations

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {
    @Bean
    fun eventQueue(): Queue {
        return Queue("eventQueue", false)
    }
}
