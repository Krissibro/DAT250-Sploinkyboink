package com.example.sploinkyboink

import java.time.Instant

data class Poll(
    val pollId: String,
    val byUser: User,
    val question: String,
    val publishedAt: Instant,
    val validUntil: Instant,
    val voteOptions: List<String>,
    val votes: MutableMap<User, Vote> = mutableMapOf()
) {
    // User votes on the poll
    fun vote(user: User, vote: Vote) {
        if (vote.voteOption in voteOptions) {
            votes[user] = vote
        } else {
            throw IllegalArgumentException("Invalid vote option")
        }
    }

    // User edits their existing vote
    fun editVote(user: User, vote: Vote) {
        if (votes.containsKey(user)) {
            vote(user, vote)
        } else {
            throw IllegalArgumentException("No existing vote to edit")
        }
    }

    // User deletes their vote
    fun deleteVote(user: User) {
        votes.remove(user)
    }

    // Get all votes for the poll
    fun getAllVotes(): MutableMap<User, Vote> {
        return votes
    }
}