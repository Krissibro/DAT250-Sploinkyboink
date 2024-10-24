package com.example.sploinkyboink.controllers

import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sploinkyboinkend")    // Aka "/api"
class UserController(
    private val userService: UserService
) {

    // Register a new user
    @PostMapping("/users")
    fun createUser(
        @RequestParam username: String,
        @RequestParam password: String,
        @RequestParam confirmPassword: String,
        @RequestParam email: String
    ): ResponseEntity<String> {
        return try {
            userService.registerUser(username, password, confirmPassword, email)
            ResponseEntity("User created", HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    // Get all users
    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<User>> {
        return ResponseEntity(userService.getAllUsers(), HttpStatus.OK)
    }

    // Get a specific user by username
    @GetMapping("/users/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<User> {
        return try {
            val user = userService.getUser(username)
            ResponseEntity(user, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Delete a user by userID
    @DeleteMapping("/users/{userID}")
    fun deleteUserById(@PathVariable userID: Long): ResponseEntity<String> {
        return try {
            userService.deleteUser(userID)
            ResponseEntity("User deleted", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }

    // Update user email
    @PutMapping("/users/{userID}/email")
    fun changeEmail(
        @PathVariable userID: Long,
        @RequestParam newEmail: String
    ): ResponseEntity<String> {
        return try {
            userService.changeEmail(userID, newEmail)
            ResponseEntity("Email updated successfully", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    // Update username
    @PutMapping("/users/{userID}/username")
    fun changeUsername(
        @PathVariable userID: Long,
        @RequestParam newUsername: String
    ): ResponseEntity<String> {
        return try {
            userService.changeUsername(userID, newUsername)
            ResponseEntity("Username updated successfully", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    // Change user password
    @PutMapping("/users/{userID}/password")
    fun changePassword(
        @PathVariable userID: Long,
        @RequestParam newPassword: String,
        @RequestParam confirmPassword: String
    ): ResponseEntity<String> {
        return try {
            userService.changePassword(userID, newPassword, confirmPassword)
            ResponseEntity("Password updated successfully", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    // Get all polls created by a user (stubbed method, needs actual implementation)
    @GetMapping("/users/{username}/polls")
    fun getPollsCreatedByUser(@PathVariable username: String): ResponseEntity<List<String>> {
        // TODO: Example stub, replace with actual service method once it's ready
        return try {
            val polls = listOf("poll1", "poll2") // Replace with actual data
            ResponseEntity(polls, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Get all polls the user has participated in (stubbed method, needs actual implementation)
    @GetMapping("/users/{username}/participated-polls")
    fun getPollsUserParticipatedIn(@PathVariable username: String): ResponseEntity<List<String>> {
        // TODO: Example stub, replace with actual service method once it's ready
        return try {
            val participatedPolls = listOf("poll3", "poll4") // Replace with actual data
            ResponseEntity(participatedPolls, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
