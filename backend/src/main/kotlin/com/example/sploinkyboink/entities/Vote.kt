package com.example.sploinkyboink.entities

import User
import java.time.Instant
import jakarta.persistence.*

@Entity
@Table(name = "votes")
data class Vote (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    val poll: Poll,

    @ManyToOne
    @JoinColumn(name = "user_username", nullable = false)
    val user: User,

    val voteOption: String,
    val publishedAt: Instant
)