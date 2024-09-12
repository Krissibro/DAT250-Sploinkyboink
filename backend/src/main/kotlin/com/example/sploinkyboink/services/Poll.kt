package com.example.sploinkyboink.services

import java.time.Instant

data class Poll(
    val pollId: String,
    val byUser: User,
    val question: String,
    val publishedAt: Instant,
    val validUntil: Instant,
    val voteOptions: List<String>,
    val votes: MutableMap<String, Vote> = mutableMapOf()
) {
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