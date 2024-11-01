package com.example.sploinkyboink.listeners

import com.example.sploinkyboink.entities.Event
import com.example.sploinkyboink.services.EventService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class EventListener(
    private val eventService: EventService
) {
    @RabbitListener(queues = ["eventQueue"])
    fun receiveMessage(event: Event) {
        eventService.logEvent(event)
    }
}