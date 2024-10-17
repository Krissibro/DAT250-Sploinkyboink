package com.example.sploinkyboink.services

import com.example.sploinkyboink.entities.Poll
import User
import com.example.sploinkyboink.entities.Vote
import com.example.sploinkyboink.repositories.PollRepository
import com.example.sploinkyboink.repositories.UserRepository
import com.example.sploinkyboink.repositories.VoteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PollService(
    private val userRepository: UserRepository,
    private val pollRepository: PollRepository,
    private val voteRepository: VoteRepository
) {

    // User Operations

    fun createUser(user: User) {
        if (!userRepository.existsById(user.username)) {
            userRepository.save(user)
        } else {
            throw IllegalArgumentException("User already exists")
        }
    }

    fun editUser(user: User) {
        if (userRepository.existsById(user.username)) {
            userRepository.save(user)
        } else {
            throw IllegalArgumentException("User not found")
        }
    }

    fun deleteUser(username: String) {
        userRepository.deleteById(username)
    }

    fun getUser(username: String): User? {
        return userRepository.findById(username).orElse(null)
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    // Poll Operations

    fun createPoll(poll: Poll) {
        if (!pollRepository.existsById(poll.pollId)) {
            pollRepository.save(poll)
        } else {
            throw IllegalArgumentException("Poll with this ID already exists")
        }
    }

    fun editPoll(poll: Poll) {
        if (pollRepository.existsById(poll.pollId)) {
            pollRepository.save(poll)
        } else {
            throw IllegalArgumentException("Poll not found")
        }
    }

    fun getPoll(pollId: String): Poll? {
        return pollRepository.findById(pollId).orElse(null)
    }

    fun getAllPolls(): List<Poll> {
        return pollRepository.findAll()
    }

    fun deletePoll(pollId: String) {
        pollRepository.deleteById(pollId)
    }

    // Vote Operations

    @Transactional
    fun userVoteOnPoll(username: String, pollId: String, voteOption: String) {
        val user = userRepository.findById(username)
            .orElseThrow { IllegalArgumentException("User not found") }
        val poll = pollRepository.findById(pollId)
            .orElseThrow { IllegalArgumentException("Poll not found") }

        if (voteOption !in poll.voteOptions) {
            throw IllegalArgumentException("Invalid vote option")
        }

        val vote = Vote(
            poll = poll,
            user = user,
            voteOption = voteOption,
            publishedAt = Instant.now()
        )
        voteRepository.save(vote)
    }

    @Transactional
    fun userEditVoteOnPoll(username: String, pollId: String, newVoteOption: String) {
        val vote = voteRepository.findByPollPollIdAndUserUsername(pollId, username)
            ?: throw IllegalArgumentException("Vote not found")

        if (newVoteOption !in vote.poll.voteOptions) {
            throw IllegalArgumentException("Invalid vote option")
        }

        val updatedVote = vote.copy(
            voteOption = newVoteOption,
            publishedAt = Instant.now()
        )
        voteRepository.save(updatedVote)
    }

    @Transactional
    fun userDeleteVoteOnPoll(username: String, pollId: String) {
        val vote = voteRepository.findByPollPollIdAndUserUsername(pollId, username)
            ?: throw IllegalArgumentException("Vote not found")
        voteRepository.delete(vote)
    }

    fun getAllVotesFromPoll(pollId: String): List<Vote> {
        return voteRepository.findByPollPollId(pollId)
    }
}
