package com.example.sploinkyboink.entities

import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["username", "email"])])
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userID: Long = 0,

    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var passwordHash: String,
) {
    // Password setting method to hash the password using BCrypt
    fun setPassword(password: String) {
        val encoder = BCryptPasswordEncoder()
        this.passwordHash = encoder.encode(password)
    }

    // Password verification
    fun checkPassword(rawPassword: String): Boolean {
        val encoder = BCryptPasswordEncoder()
        return encoder.matches(rawPassword, this.passwordHash)
    }
}
