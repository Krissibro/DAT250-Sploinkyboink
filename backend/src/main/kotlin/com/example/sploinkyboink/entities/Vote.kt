package com.example.sploinkyboink.entities

import java.time.Instant
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@Entity
@Table(name = "votes")
data class Vote (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @JoinColumn(name = "poll_id", nullable = false)
    val pollID: String,

    @JoinColumn(name = "user_id", nullable = false)
    val userID: Long,

    var voteOption: String,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val publishedAt: Instant? = null,
    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedAt: Instant? = null
)