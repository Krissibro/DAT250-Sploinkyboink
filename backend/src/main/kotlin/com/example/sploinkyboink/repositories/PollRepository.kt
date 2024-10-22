package com.example.sploinkyboink.repositories

import com.example.sploinkyboink.entities.Poll
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface PollRepository : JpaRepository<Poll, String> {
    // Custom query to get all active polls (validUntil is in the future)
    fun findByValidUntilAfter(now: Instant, pageable: Pageable): Page<Poll>
}
