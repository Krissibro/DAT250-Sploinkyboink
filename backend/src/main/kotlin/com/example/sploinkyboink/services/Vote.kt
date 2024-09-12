package com.example.sploinkyboink.services

import java.time.Instant

data class Vote (
    val voteOption: String,
    val publishedAt: Instant,
    ) {
}