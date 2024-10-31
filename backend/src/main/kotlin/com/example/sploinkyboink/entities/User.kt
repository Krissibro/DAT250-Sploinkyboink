package com.example.sploinkyboink.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["username", "email"])])
@JsonIgnoreProperties(ignoreUnknown = true, value = ["hibernateLazyInitializer", "handler"])
data class User(
    @Id
    val userID: Long = 0,

    @Column(nullable = false, unique = true)
    var username: String,

    @JsonIgnore
    @Column(nullable = false, unique = true)
    var email: String,

    @JsonIgnore
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
