package com.example.sploinkyboink.services

import com.example.sploinkyboink.entities.Event
import com.example.sploinkyboink.entities.Poll
import com.example.sploinkyboink.repositories.EventRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val rabbitTemplate: RabbitTemplate,
) {
    fun logEvent(event: Event) {
        val savedEvent = eventRepository.save(event)  // Save to get the generated ID
        println("Received event with ID: ${savedEvent.id}, details: $savedEvent")
    }

    fun sendVoteEvent(poll: Poll) {
        // TODO: Example event, add more info?
        val event = Event(
            type = "VoteEvent",
            details = mapOf(
                "pollID" to poll.pollID,
                "question" to poll.question,
                "options" to poll.voteOptions,
                "voteCounts" to poll.votes.groupingBy { it.voteOption }.eachCount()  // Aggregated vote counts
            )
        )
        rabbitTemplate.convertAndSend("eventQueue", event)
    }

    fun sendPollCreatedEvent(poll: Poll) {
        val event = Event(
            type = "PollCreated",
            details = mapOf(
                "pollID" to poll.pollID,
                "byUser" to (poll.byUser ?: "Unknown"),
                "question" to poll.question,
                "voteOptions" to poll.voteOptions,
                "publishedAt" to (poll.publishedAt ?: Instant.now()),
                "validUntil" to poll.validUntil
            )
        )
        rabbitTemplate.convertAndSend("eventQueue", event)
    }

    fun sendPollEditedEvent(poll: Poll) {
        val event = Event(
            type = "PollEdited",
            details = mapOf(
                "pollID" to poll.pollID,
                "byUser" to (poll.byUser ?: "Unknown"),
                "question" to poll.question,
                "voteOptions" to poll.voteOptions,
                "lastModifiedAt" to (poll.lastModifiedAt ?: Instant.now()),
                "validUntil" to poll.validUntil
            )
        )
        rabbitTemplate.convertAndSend("eventQueue", event)
    }
}
