package com.example.sploinkyboink.services

import org.springframework.stereotype.Service

@Service
class PollService {
    private val users = mutableMapOf<String, User>()
    private val polls = mutableMapOf<String, Poll>()

    // Creates a new user
    fun createUser(user: User) {
        if (!users.containsKey(user.username)) {
            users[user.username] = user
        } else {
            throw IllegalArgumentException("User already exists")
        }
    }

    // Edits an existing user
    fun editUser(user: User) {
        if (users.containsKey(user.username)) {
            users[user.username] = user
        } else {
            throw IllegalArgumentException("User not found")
        }
    }

    // Deletes a user
    fun deleteUser(user: User) {
        users.remove(user.username)
    }

    // Retrieves a user by username
    fun getUser(username: String): User? {
        return users[username]
    }

    // Lists all users
    fun getAllUsers(): MutableCollection<User> {
        return users.values
    }

    // Creates a new poll
    fun createPoll(user: User, poll: Poll) {
        if (polls.containsKey(poll.pollId)) {
            throw IllegalArgumentException("Poll with this ID already exists")
        } else {
            polls[poll.pollId] = poll
        }
    }

    // Edits an existing poll
    fun editPoll(poll: Poll) {
        if (polls.containsKey(poll.pollId)) {
            polls[poll.pollId] = poll
        } else {
            throw IllegalArgumentException("Poll not found")
        }
    }

    // Retrieves a poll by pollId
    fun getPoll(pollId: String): Poll? {
        return polls[pollId]
    }

    // Lists all polls
    fun getAllPolls(): MutableCollection<Poll> {
        return polls.values
    }

    // Deletes a poll
    fun deletePoll(pollId: String) {
        polls.remove(pollId)
    }

    // User votes on a poll
    fun userVoteOnPoll(user: User, pollId: String, vote: Vote) {
        val poll = getPoll(pollId)
        if (poll != null) {
            poll.vote(user, vote)
        } else {
            throw IllegalArgumentException("Poll not found")
        }
    }

    // User edits their vote on a poll
    fun userEditVoteOnPoll(user: User, pollId: String, vote: Vote) {
        val poll = getPoll(pollId)
        if (poll != null) {
            poll.editVote(user, vote)
        } else {
            throw IllegalArgumentException("Poll not found")
        }
    }

    // User deletes their vote on a poll
    fun userDeleteVoteOnPoll(user: User, pollId: String) {
        val poll = getPoll(pollId)
        if (poll != null) {
            poll.deleteVote(user)
        } else {
            throw IllegalArgumentException("Poll not found")
        }
    }

    // Lists all votes in a poll
    fun getAllVotesFromPoll(pollId: String): MutableMap<String, Vote>? {
        val poll = getPoll(pollId)
        return poll?.getAllVotes()
    }
}