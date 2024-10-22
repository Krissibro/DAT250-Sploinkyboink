package com.example.sploinkyboink.controllers

import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sploinkyboinkend")    // Aka "/api"
class UserController (
    private val userService: UserService
) {

    // Create a new user
    @PostMapping("/users")
    fun createUser(
        @RequestParam username: String,
        @RequestParam email: String
    ): ResponseEntity<String> {
        val user = User(username, email)
        return try {
            userService.createUser(user)
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

    // Delete a user by username
    @DeleteMapping("/users/{username}")
    fun deleteUserByUsername(@PathVariable username: String): ResponseEntity<String> {
        return try {
            userService.deleteUser(username)
            ResponseEntity("User deleted", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }

    // Edit/update user information
    @PutMapping("/users/{username}")
    fun updateUser(
        @PathVariable username: String,
        @RequestParam email: String
    ): ResponseEntity<String> {
        return try {
            val updatedUser = User(username, email)
            userService.editUser(username, updatedUser)
            ResponseEntity("User updated", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        }
    }


    // Additional endpoints for polls related to the user

    // Get all polls created by a user
    @GetMapping("/users/{username}/polls")
    fun getPollsCreatedByUser(@PathVariable username: String): ResponseEntity<List<String>> {
        // Assuming there is a service method to get polls by user
        return try {
            val polls = listOf("poll1", "poll2") // Example data, replace with actual service method
            ResponseEntity(polls, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Get all polls the user has voted in or participated in
    @GetMapping("/users/{username}/participated-polls")
    fun getPollsUserParticipatedIn(@PathVariable username: String): ResponseEntity<List<String>> {
        // Assuming there is a service method to get polls user voted in
        return try {
            val participatedPolls = listOf("poll3", "poll4") // Example data, replace with actual service method
            ResponseEntity(participatedPolls, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
