package com.example.sploinkyboink.services

import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    // TODO: NEEDS actual JwtToken validation function which can be used in Controller to validate the session token

    fun validatePasswordStrength(password: String) {
        if (password.length < 8) {
            throw IllegalArgumentException("Password must be at least 8 characters long")
        }
        if (!password.any { it.isDigit() }) {
            throw IllegalArgumentException("Password must contain at least one digit")
        }
        if (!password.any { it.isUpperCase() }) {
            throw IllegalArgumentException("Password must contain at least one uppercase letter")
        }
        // TODO: Add more rules maybe?
    }


    @Transactional
    fun registerUser(username: String, password: String, confirmPassword: String, email: String): User {
        if (password != confirmPassword) {
            throw IllegalArgumentException("Passwords do not match")
        }
        if (userRepository.findByUsername(username) != null) {
            throw IllegalArgumentException("Username $username is already taken")
        }
        if (userRepository.findByEmail(email) != null) {
            throw IllegalArgumentException("Email $email is already in use")
        }

        validatePasswordStrength(password)

        val user = User(username = username, passwordHash = "", email = email)
        user.setPassword(password)  // Hash the password before saving
        return userRepository.save(user)
    }

    fun authenticate(login: String, rawPassword: String): Boolean {
        // Check if the input is an email or username
        val user = if (login.contains("@")) {
            // If the input contains '@', treat it as an email
            userRepository.findByEmail(login)
                ?: throw IllegalArgumentException("User with email $login not found")
        } else {
            // Otherwise, treat it as a username
            userRepository.findByUsername(login)
                ?: throw IllegalArgumentException("User with username $login not found")
        }

        return user.checkPassword(rawPassword)  // Check hashed password
    }

    // Retrieve a user by their username
    fun getUser(username: String): User? {
        return userRepository.findByUsername(username)
    }

    // Retrieve all users
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    // Delete a user by their username
    @Transactional
    fun deleteUser(userID: Long) {
        if (!userRepository.existsById(userID)) {
            throw IllegalArgumentException("User with userID $userID does not exist")
        }
        userRepository.deleteById(userID)
    }

    // Edit a user's details (more efficient update instead of delete and save)
    @Transactional
    fun changeEmail(userID: Long, newEmail: String): User {
        val existingUser = userRepository.findById(userID)
            .orElseThrow { IllegalArgumentException("User with userID $userID not found") }

        if (userRepository.findByEmail(newEmail) != null) {
            throw IllegalArgumentException("Email $newEmail is already in use")
        }

        existingUser.email = newEmail
        return userRepository.save(existingUser)
    }

    @Transactional
    fun changeUsername(userID: Long, newUsername: String): User {
        val existingUser = userRepository.findById(userID)
            .orElseThrow { IllegalArgumentException("User with userID $userID not found") }

        if (userRepository.findByUsername(newUsername) != null) {
            throw IllegalArgumentException("Username $newUsername is already taken")
        }

        existingUser.username = newUsername
        return userRepository.save(existingUser)
    }

    @Transactional
    fun changePassword(userID: Long, newPassword: String, confirmPassword: String): User {
        if (newPassword != confirmPassword) {
            throw IllegalArgumentException("Passwords do not match")
        }
        validatePasswordStrength(newPassword)

        val existingUser = userRepository.findById(userID)
            .orElseThrow { IllegalArgumentException("User with userID $userID not found") }

        existingUser.setPassword(newPassword)  // Hash the new password
        return userRepository.save(existingUser)
    }


    // Check if a user exists by their username
    fun userExists(userID: Long): Boolean {
        return userRepository.existsById(userID)
    }
}
