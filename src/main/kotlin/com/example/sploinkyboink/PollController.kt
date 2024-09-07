package com.example.sploinkyboink

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
    fun createUser(@RequestBody user: User): ResponseEntity<String> {
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

    @PutMapping("/users/{username}")
    fun editUser(@PathVariable username: String, @RequestBody updatedUser: User): ResponseEntity<String> {
        return try {
            pollService.editUser(updatedUser)
            ResponseEntity("User updated", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
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
    fun createPoll(@RequestParam username: String, @RequestBody poll: Poll): ResponseEntity<String> {
        val user = pollService.getUser(username)
        return if (user != null) {
            pollService.createPoll(user, poll)
            ResponseEntity("Poll created", HttpStatus.CREATED)
        } else {
            ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/polls")
    fun getAllPolls(): ResponseEntity<List<Poll>> {
        return ResponseEntity(pollService.getAllPolls().toList(), HttpStatus.OK)
    }

    @GetMapping("/polls/{pollId}")
    fun getPoll(@PathVariable pollId: String): ResponseEntity<Poll> {
        val poll = pollService.getPoll(pollId)
        return if (poll != null) {
            ResponseEntity(poll, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/polls/{pollId}")
    fun editPoll(@PathVariable pollId: String, @RequestBody updatedPoll: Poll): ResponseEntity<String> {
        return try {
            pollService.editPoll(updatedPoll)
            ResponseEntity("Poll updated", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
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

    // VOTING ENDPOINTS ---------------------

    @PostMapping("/polls/{pollId}/vote")
    fun voteOnPoll(
        @PathVariable pollId: String,
        @RequestParam username: String,
        @RequestBody voteOption: String
    ): ResponseEntity<String> {
        val user = pollService.getUser(username)
        return if (user != null) {
            try {
                val vote = Vote(voteOption, Instant.now())
                pollService.userVoteOnPoll(user, pollId, vote)
                ResponseEntity("Vote registered", HttpStatus.OK)
            } catch (e: IllegalArgumentException) {
                ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
            }
        } else {
            ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/polls/{pollId}/vote")
    fun editVoteOnPoll(
        @PathVariable pollId: String,
        @RequestParam username: String,
        @RequestBody voteOption: String
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

    @DeleteMapping("/polls/{pollId}/vote")
    fun deleteVoteOnPoll(
        @PathVariable pollId: String,
        @RequestParam username: String
    ): ResponseEntity<String> {
        val user = pollService.getUser(username)
        return if (user != null) {
            try {
                pollService.userDeleteVoteOnPoll(user, pollId)
                ResponseEntity("Vote deleted", HttpStatus.OK)
            } catch (e: IllegalArgumentException) {
                ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
            }
        } else {
            ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/polls/{pollId}/votes")
    fun getAllVotesInPoll(@PathVariable pollId: String): ResponseEntity<Map<User, Vote>> {
        val votes = pollService.getAllVotesFromPoll(pollId)
        return if (votes != null) {
            ResponseEntity(votes, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
