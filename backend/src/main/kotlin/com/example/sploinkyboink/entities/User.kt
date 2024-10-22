package com.example.sploinkyboink.entities

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    val username: String,

    @Column(nullable = false, unique = true)
    var email: String
)