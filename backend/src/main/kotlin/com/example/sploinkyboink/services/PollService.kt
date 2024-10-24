package com.example.sploinkyboink.services

import com.example.sploinkyboink.entities.Poll
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
    private val userService: UserService  // Dependency to access User-related operations
) {
    // Poll creation, now with byUserID
    fun createPoll(byUserID: Long, question: String, voteOptions: List<String>, validUntil: Instant): Poll {
        if (!userService.userExists(byUserID)) {
            throw IllegalArgumentException("User with userID $byUserID not found")
        }

        val pollId = "poll_${System.currentTimeMillis()}"
        val poll = Poll(
            pollID = pollId,
            byUserID = byUserID,
            question = question,
            publishedAt = Instant.now(),
            lastModifiedAt = Instant.now(),
            validUntil = validUntil,
            voteOptions = voteOptions
        )

        return pollRepository.save(poll)
    }

    // Get a poll by ID
    fun getPollById(pollID: String): Poll {
        return pollRepository.findById(pollID).orElseThrow { IllegalArgumentException("Poll not found") }
    }

    // Get all polls with pagination
    fun getAllPolls(page: Int, size: Int): Page<Poll> {
        val pageable = PageRequest.of(page, size)
        return pollRepository.findAll(pageable)
    }

    // Get all active polls (those whose validUntil is in the future)
    fun getAllActivePolls(page: Int, size: Int): Page<Poll> {
        val pageable: Pageable = PageRequest.of(page, size)
        return pollRepository.findByValidUntilAfter(Instant.now(), pageable)
    }

    // Delete a poll
    @Transactional
    fun deletePoll(pollID: String) {
        pollRepository.deleteById(pollID)
    }

    // Edit a poll (update allowed fields)
    @Transactional
    fun editPoll(pollID: String, updatedQuestion: String, updatedVoteOptions: List<String>, updatedValidUntil: Instant): Poll {
        val existingPoll = pollRepository.findById(pollID)
            .orElseThrow { IllegalArgumentException("Poll not found") }

        existingPoll.question = updatedQuestion
        existingPoll.validUntil = updatedValidUntil
        existingPoll.voteOptions = updatedVoteOptions

        return pollRepository.save(existingPoll)
    }

    // Get poll results (voting results)
    fun getPollResults(pollID: String): MutableMap<String, Long> {
        val poll = pollRepository.findById(pollID).orElseThrow {
            IllegalArgumentException("Poll with ID $pollID not found")
        }

        // Group the votes by the vote option, count occurrences, and map the results
        return poll.votes.groupingBy { it.voteOption }.eachCount().mapValues { it.value.toLong() }.toMutableMap()
    }


    // Voting logic

    @Transactional
    fun voteOnPoll(pollID: String, userID: Long, voteOption: String): Vote {
        val poll = getPollById(pollID)
        val user = userService.getUserByUserID(userID)
            ?: throw IllegalArgumentException("User with userID $userID not found")

        // Check if the user has already voted
        if (poll.votes.any { it.userID == user.userID }) {
            throw IllegalStateException("User has already voted in this poll")
        }

        // Check if the poll is still active
        if (Instant.now().isAfter(poll.validUntil)) {
            throw IllegalStateException("Poll has expired")
        }

        // Validate vote option
        if (voteOption !in poll.voteOptions) {
            throw IllegalArgumentException("Invalid vote option")
        }

        // Create and save the vote
        val vote = Vote(pollID = poll.pollID, userID = user.userID, voteOption = voteOption)
        poll.votes.add(vote)
        voteRepository.save(vote)

        return vote
    }

    // Edit vote on poll
    @Transactional
    fun editVote(pollID: String, userID: Long, newVoteOption: String): Vote {
        val poll = getPollById(pollID)
        val existingVote = poll.votes.find { it.userID == userID }
            ?: throw IllegalArgumentException("No existing vote to edit")

        if (newVoteOption !in poll.voteOptions) {
            throw IllegalArgumentException("Invalid vote option")
        }

        existingVote.voteOption = newVoteOption
        return voteRepository.save(existingVote)
    }

    // Delete a user's vote from a poll
    @Transactional
    fun deleteVote(pollID: String, userID: Long) {
        val poll = getPollById(pollID)
        val existingVote = poll.votes.find { it.userID == userID }
            ?: throw IllegalArgumentException("No existing vote to delete")

        poll.votes.remove(existingVote)
        voteRepository.delete(existingVote)
    }
}
