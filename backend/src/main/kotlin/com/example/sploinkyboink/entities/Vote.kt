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

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    val poll: Poll,

    @ManyToOne
    @JoinColumn(name = "user_username", nullable = false)
    val user: User,

    var voteOption: String,
    @CreatedDate
    var publishedAt: Instant,
    @LastModifiedDate
    var lastModifiedAt: Instant
)