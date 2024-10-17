package com.example.sploinkyboink.entities

import User
import java.time.Instant
import jakarta.persistence.*

@Entity
@Table(name = "polls")
data class Poll(
    @Id
    val pollId: String,

    @ManyToOne
    @JoinColumn(name = "user_username", nullable = false)
    val byUser: User,

    val question: String,
    val publishedAt: Instant,
    val validUntil: Instant,

    @ElementCollection
    @CollectionTable(name = "poll_vote_options", joinColumns = [JoinColumn(name = "poll_id")])
    @Column(name = "vote_option")
    val voteOptions: List<String>
) {
    @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], orphanRemoval = true)
    val votes: MutableSet<Vote> = mutableSetOf()
    // User votes on the poll
    fun vote(user: User, vote: Vote) {
        if (vote.voteOption in voteOptions) {
            votes[user.username] = vote
        } else {
            throw IllegalArgumentException("Invalid vote option")
        }
    }

    // User edits their existing vote
    fun editVote(user: User, vote: Vote) {
        if (votes.containsKey(user.username)) {
            vote(user, vote)
        } else {
            throw IllegalArgumentException("No existing vote to edit")
        }
    }

    // User deletes their vote
    fun deleteVote(user: User) {
        votes.remove(user.username)
    }

    // Get all votes for the poll
    fun getAllVotes(): MutableMap<String, Vote> {
        return votes
    }
}