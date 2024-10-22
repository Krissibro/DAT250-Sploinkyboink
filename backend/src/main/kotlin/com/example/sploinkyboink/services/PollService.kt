package com.example.sploinkyboink.services

import com.example.sploinkyboink.entities.Poll
import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.entities.Vote
import com.example.sploinkyboink.repositories.PollRepository
import com.example.sploinkyboink.repositories.VoteRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class PollService(
    private val pollRepository: PollRepository,
    private val voteRepository: VoteRepository,
) {
    // Poll repository functions
    fun createPoll(poll: Poll): Poll {
        pollRepository.save(poll)
        return poll
    }

    fun getPollById(pollID: String): Poll {
        return pollRepository.findById(pollID).orElseThrow { IllegalArgumentException("Poll not found") }
    }

    fun getAllPolls(): List<Poll> {
        return pollRepository.findAll()
    }

    fun deletePoll(pollID: String) {
        pollRepository.deleteById(pollID)
    }

    @Transactional
    fun editPoll(pollID: String, updatedPoll: Poll): Poll {
        val existingPoll = pollRepository.findById(pollID).orElseThrow { IllegalArgumentException("Poll not found") }

        // Update fields that are allowed to change
        existingPoll.question = updatedPoll.question
        existingPoll.validUntil = updatedPoll.validUntil
        existingPoll.voteOptions = updatedPoll.voteOptions

        // You can add any other fields that should be updated here
        return pollRepository.save(existingPoll)
    }

    fun getPollResults(poll: Poll): Map<String, Long> {
        // TODO: add get from database here maybe?
        return poll.votes.groupingBy { it.voteOption }.eachCount().mapValues { it.value.toLong() }
    }

    fun getAllPolls(page: Int, size: Int): Page<Poll> {
        val pageable = PageRequest.of(page, size)
        return pollRepository.findAll(pageable)
    }

    fun getAllActivePolls(page: Int, size: Int): Page<Poll> {
        val pageable: Pageable = PageRequest.of(page, size)
        return pollRepository.findByValidUntilAfter(Instant.now(), pageable)
    }


    // Voting functions
    @Transactional
    fun voteOnPoll(poll: Poll, user: User, vote: Vote) {
        if (poll.votes.any { it.user.username == user.username }) {
            throw IllegalStateException("User has already voted in this poll")
        }

        if (Instant.now().isAfter(poll.validUntil)) {
            throw IllegalStateException("Poll has expired")
        }

        if (vote.voteOption in poll.voteOptions) {
            poll.votes.add(vote)
            voteRepository.save(vote)
        } else {
            throw IllegalArgumentException("Invalid vote option")
        }
    }

    @Transactional
    fun editVote(poll: Poll, user: User, newVote: Vote) {
        val existingVote = poll.votes.find { it.user.username == user.username }
        if (existingVote != null) {
            existingVote.voteOption = newVote.voteOption
            voteRepository.save(existingVote)
        } else {
            throw IllegalArgumentException("No existing vote to edit")
        }
    }

    @Transactional
    fun deleteVote(poll: Poll, user: User) {
        val existingVote = poll.votes.find { it.user.username == user.username }
        if (existingVote != null) {
            poll.votes.remove(existingVote)
            voteRepository.delete(existingVote)
        } else {
            throw IllegalArgumentException("No existing vote to delete")
        }
    }
}
