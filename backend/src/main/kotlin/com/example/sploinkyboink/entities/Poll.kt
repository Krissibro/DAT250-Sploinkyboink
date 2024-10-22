package com.example.sploinkyboink.entities

import java.time.Instant
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@Entity
@Table(name = "polls")
data class Poll(
    @Id
    val pollId: String,

    @ManyToOne
    @JoinColumn(name = "user_username", nullable = false)
    val byUser: User,

    var question: String,

    @CreatedDate
    val publishedAt: Instant,
    @LastModifiedDate
    val lastModifiedAt: Instant,
    var validUntil: Instant,

    @ElementCollection
    @CollectionTable(name = "poll_vote_options", joinColumns = [JoinColumn(name = "poll_id")])
    @Column(name = "vote_option")
    var voteOptions: List<String>,

    @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], orphanRemoval = true)
    val votes: MutableSet<Vote> = mutableSetOf()
)