package com.example.sploinkyboink.entities

import java.time.Instant
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference

@Entity
@Table(name = "polls")
data class Poll(
    @Id
    val pollID: String,

    @ManyToOne(fetch = FetchType.LAZY)  // Map to the User entity
    @JoinColumn(name = "user_id", nullable = false)  // Reference to the User table
    @JsonIgnore
    val byUser: User?,

    var question: String,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val publishedAt: Instant? = null,

    @LastModifiedDate
    @Column(nullable = false)
    val lastModifiedAt: Instant? = null,
    var validUntil: Instant,

    @ElementCollection
    @CollectionTable(name = "poll_vote_options", joinColumns = [JoinColumn(name = "poll_id")])
    @Column(name = "vote_option")
    var voteOptions: List<String>,

    @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    val votes: MutableSet<Vote> = mutableSetOf()
) {
    override fun equals(other: Any?) = other is Poll && other.pollID == this.pollID
    override fun hashCode() = pollID.hashCode()
}