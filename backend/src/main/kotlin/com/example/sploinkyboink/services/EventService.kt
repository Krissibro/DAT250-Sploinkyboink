package com.example.sploinkyboink.services

import com.example.sploinkyboink.entities.Event
import com.example.sploinkyboink.repositories.EventRepository
import org.springframework.stereotype.Service

@Service
class EventService(
    private val eventRepository: EventRepository
) {
    fun logEvent(event: Event) {
        eventRepository.save(event)
    }
}
