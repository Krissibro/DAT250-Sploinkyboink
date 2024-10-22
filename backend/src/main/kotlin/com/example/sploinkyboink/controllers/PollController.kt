package com.example.sploinkyboink.controllers

import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.entities.Poll
import com.example.sploinkyboink.services.PollService
import com.example.sploinkyboink.entities.Vote
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/sploinkyboinkend")         // aka "/api"
class PollController(
    private val pollService: PollService
) {

    @PostMapping("/polls")      // "/create-poll"
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
                lastModifiedAt = Instant.now(),
                voteOptions = voteOptions
            )
            pollService.createPoll(poll)
            ResponseEntity("Poll created with ID: $pollId", HttpStatus.CREATED)
        } else {
            ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
    }


    @GetMapping("/polls")       // "/get-all-polls", "/get-polls", "/retrieve-polls"
    fun getAllPolls(): ResponseEntity<List<Poll>> {
        return ResponseEntity(pollService.getAllPolls().toList(), HttpStatus.OK)
    }

    @PostMapping("/polls/{pollId}/vote")    // "/polls/{pollID}/submit-vote" or "/polls/{pollID}/cast-vote"
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

    @PutMapping("/polls/{pollId}/vote")     // "/polls/{pollID}/edit-vote"
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

    @GetMapping("/polls/{pollId}/votes")        // "/polls/{pollID}/get-poll-votes" or "/polls/{pollID}/get-votes"
    fun getAllVotesInPoll(@PathVariable pollId: String): ResponseEntity<MutableMap<String, Vote>> {
        val votes = pollService.getAllVotesFromPoll(pollId)
        return if (votes != null) {
            ResponseEntity(votes, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Maybe another mapping that allows us to see who has voted on an option (given that the poll was not anonymous, option to consider, toggle when creating poll?),
    // could be included in previous or @GetMapping("/polls/{pollID}/voters"). Break down by vote option?

    @DeleteMapping("/polls/{pollId}")       // "/polls/{pollID}/delete-poll"
    fun deletePoll(@PathVariable pollId: String): ResponseEntity<String> {
        return try {
            pollService.deletePoll(pollId)
            ResponseEntity("Poll deleted", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }

    // Additional endpoint to retrieve the summarized results of a poll (for when a poll is over)? @GetMapping("/polls/{pollID}/results")

    // Additional endpoint to close a poll before its over or reopen it? May necessitate a change in Poll.kt, some way of checking if a poll is active
    // @PutMapping("/polls/{pollID}/close") and @PutMapping("/polls/{pollID}/reopen") ?

    // Additional endpoint for searching or filtering polls, some search mapping with filters like:
    // keyword to search for,
    // creator (based on username?), => Many need an endpoint to check if a username is in use or not (or mail, for user creation too?)
    // if they are active or not,
    // date of creation and expiry (before or after the given dates),
    // popularity (number of votes or how many votes it has gotten within a timespan) => Some way to return a measure for how popular a poll is (useful for display on frontpage?)

    // Endpoints to add data to and retrieve it from our analytics database, f.ex. see voting trends for a poll over time, TBD

    // Endpoints for admin usage? Dunno if necessary. E.g. get statistics about users and polls, ability for admin to delete votes, polls and users (and all associated data)
}
