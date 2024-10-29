package com.example.sploinkyboink.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "events")
data class Event(
    @Id val id: String? = null,
    val timestamp: Instant = Instant.now(),
    val type: String,
    val details: Map<String, Any>
)