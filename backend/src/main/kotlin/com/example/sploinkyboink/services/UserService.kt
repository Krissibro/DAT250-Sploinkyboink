package com.example.sploinkyboink.services

import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    // Create a new user if the username doesn't already exist
    @Transactional
    fun createUser(user: User): User {
        if (userRepository.existsById(user.username)) {
            throw IllegalArgumentException("User with username ${user.username} already exists")
        }
        return userRepository.save(user)
    }

    // Retrieve a user by their username
    fun getUser(username: String): User {
        return userRepository.findById(username)
            .orElseThrow { IllegalArgumentException("User with username $username not found") }
    }

    // Retrieve all users
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    // Delete a user by their username
    @Transactional
    fun deleteUser(username: String) {
        if (!userRepository.existsById(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }
        userRepository.deleteById(username)
    }

    // Edit a user's details (more efficient update instead of delete and save)
    @Transactional
    fun editUser(username: String, newUser: User): User {
        val existingUser = userRepository.findById(username)
            .orElseThrow { IllegalArgumentException("User with username $username not found") }

        // Update only the fields that need to change
        existingUser.email = newUser.email

        return userRepository.save(existingUser)
    }

    // Check if a user exists by their username
    fun userExists(username: String): Boolean {
        return userRepository.existsById(username)
    }
}
