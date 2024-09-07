package com.example.sploinkyboink

import java.time.Instant

data class Vote (
    val voteOption: String,
    val publishedAt: Instant,
    ) {
}