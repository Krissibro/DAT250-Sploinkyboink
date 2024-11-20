package com.example.sploinkyboink.controllers

import com.example.sploinkyboink.entities.Poll
import com.example.sploinkyboink.services.PollService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/sploinkyboinkend")         // aka "/api"
class PollController(
    private val pollService: PollService
) {

    @PostMapping("/polls")
    fun createPoll(
        @RequestParam userID: Long,
        @RequestParam question: String,
        @RequestParam voteOptions: List<String>,
        @RequestParam validUntil: Instant
    ): ResponseEntity<String> {
        return try {
            val poll = pollService.createPoll(userID, question, voteOptions, validUntil)
            ResponseEntity("Poll created with ID: ${poll.pollID}", HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/polls")
    fun getAllPolls(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<Poll>> {
        val polls = pollService.getAllPolls(page, size)
        return ResponseEntity(polls, HttpStatus.OK)
    }

    // Get all active polls (where validUntil is in the future)
    @GetMapping("/polls/active")
    fun getAllActivePolls(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<Poll>> {
        val activePolls = pollService.getAllActivePolls(page, size)
        return ResponseEntity(activePolls, HttpStatus.OK)
    }

    @GetMapping("/polls/{pollId}")
    fun getPoll(
        @PathVariable pollId: String
    ): ResponseEntity<Poll> {
        return ResponseEntity(pollService.getPollById(pollId), HttpStatus.OK)
    }

    @PostMapping("/polls/{pollID}/vote")
    fun voteOnPoll(
        @PathVariable pollID: String,
        @RequestParam userID: Long,
        @RequestParam voteOption: String
    ): ResponseEntity<String> {
        return try {
            pollService.voteOnPoll(pollID, userID, voteOption)
            ResponseEntity("Vote registered successfully", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        } catch (e: IllegalStateException) {
            ResponseEntity(e.message, HttpStatus.CONFLICT)  // e.g., already voted or poll expired
        }
    }

    @PutMapping("/polls/{pollID}/vote")
    fun editVoteOnPoll(
        @PathVariable pollID: String,
        @RequestParam userID: Long,
        @RequestParam newVoteOption: String
    ): ResponseEntity<String> {
        return try {
            pollService.editVote(pollID, userID, newVoteOption)
            ResponseEntity("Vote updated successfully", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    // Delete a vote from a poll
    @DeleteMapping("/polls/{pollID}/vote")
    fun deleteVoteOnPoll(
        @PathVariable pollID: String,
        @RequestParam userID: Long
    ): ResponseEntity<String> {
        return try {
            pollService.deleteVote(pollID, userID)
            ResponseEntity("Vote deleted", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("/polls/{pollID}")
    fun deletePoll(@PathVariable pollID: String): ResponseEntity<String> {
        return try {
            pollService.deletePoll(pollID)
            ResponseEntity("Poll deleted", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/polls/{pollID}")
    fun editPoll(
        @PathVariable pollID: String,
        @RequestParam question: String,
        @RequestParam voteOptions: List<String>,
        @RequestParam validUntil: Instant
    ): ResponseEntity<String> {
        return try {
            pollService.editPoll(pollID, question, voteOptions, validUntil)
            ResponseEntity("Poll updated successfully", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    // Additional endpoint for summarized poll results
    @GetMapping("/polls/{pollID}/results")
    fun getPollResults(@PathVariable pollID: String): ResponseEntity<Any> {
        return try {
            val results = pollService.getPollResults(pollID)
            ResponseEntity(results, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }
}






