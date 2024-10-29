package com.example.sploinkyboink.controllers


import com.example.sploinkyboink.services.PollService
import com.example.sploinkyboink.services.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class UserController (
    private val pollService: PollService
) {

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


}