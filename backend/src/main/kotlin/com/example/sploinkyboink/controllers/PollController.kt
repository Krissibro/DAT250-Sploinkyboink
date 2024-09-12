package com.example.sploinkyboink.controllers

import com.example.sploinkyboink.services.Poll
import com.example.sploinkyboink.services.PollService
import com.example.sploinkyboink.services.User
import com.example.sploinkyboink.services.Vote
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api")
class PollController(
    private val pollService: PollService
) {

    // USER ENDPOINTS -----------------------

    @PostMapping("/users")
    fun createUser(
        @RequestParam username: String,
        @RequestParam email: String
    ): ResponseEntity<String> {
        val user = User(username, email)
        return try {
            pollService.createUser(user)
            ResponseEntity("User created", HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<User>> {
        return ResponseEntity(pollService.getAllUsers().toList(), HttpStatus.OK)
    }

    @GetMapping("/users/{username}")
    fun getUser(@PathVariable username: String): ResponseEntity<User> {
        val user = pollService.getUser(username)
        return if (user != null) {
            ResponseEntity(user, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/users/{username}")
    fun deleteUser(@PathVariable username: String): ResponseEntity<String> {
        return try {
            pollService.deleteUser(pollService.getUser(username)!!)
            ResponseEntity("User deleted", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }

    // POLL ENDPOINTS -----------------------

    @PostMapping("/polls")
    fun createPoll(
        @RequestParam username: String,
        @RequestParam question: String,
        @RequestParam voteOptions: List<String>
    ): ResponseEntity<String> {
        val user = pollService.getUser(username)
        return if (user != null) {
            val pollId = "poll_${System.currentTimeMillis()}"
            val poll = Poll(
                pollId = pollId,
                byUser = user,
                question = question,
                publishedAt = Instant.now(),
                validUntil = Instant.now().plusSeconds(3600),
                voteOptions = voteOptions
            )
            pollService.createPoll(user, poll)
            ResponseEntity("Poll created with ID: $pollId", HttpStatus.CREATED)
        } else {
            ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
    }


    @GetMapping("/polls")
    fun getAllPolls(): ResponseEntity<List<Poll>> {
        return ResponseEntity(pollService.getAllPolls().toList(), HttpStatus.OK)
    }

    @PostMapping("/polls/{pollId}/vote")
    fun voteOnPoll(
        @PathVariable pollId: String,
        @RequestParam username: String,
        @RequestParam voteOption: String
    ): ResponseEntity<String> {
        val user = pollService.getUser(username)
        return if (user != null) {
            val vote = Vote(voteOption, Instant.now())
            pollService.userVoteOnPoll(user, pollId, vote)
            ResponseEntity("Vote registered", HttpStatus.OK)
        } else {
            ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/polls/{pollId}/vote")
    fun editVoteOnPoll(
        @PathVariable pollId: String,
        @RequestParam username: String,
        @RequestParam voteOption: String
    ): ResponseEntity<String> {
        val user = pollService.getUser(username)
        return if (user != null) {
            try {
                val updatedVote = Vote(voteOption, Instant.now())
                pollService.userEditVoteOnPoll(user, pollId, updatedVote)
                ResponseEntity("Vote updated", HttpStatus.OK)
            } catch (e: IllegalArgumentException) {
                ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
            }
        } else {
            ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/polls/{pollId}/votes")
    fun getAllVotesInPoll(@PathVariable pollId: String): ResponseEntity<MutableMap<String, Vote>> {
        val votes = pollService.getAllVotesFromPoll(pollId)
        return if (votes != null) {
            ResponseEntity(votes, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/polls/{pollId}")
    fun deletePoll(@PathVariable pollId: String): ResponseEntity<String> {
        return try {
            pollService.deletePoll(pollId)
            ResponseEntity("Poll deleted", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }
}
