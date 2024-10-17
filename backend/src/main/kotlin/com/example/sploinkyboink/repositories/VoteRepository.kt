package com.example.sploinkyboink.repositories

import com.example.sploinkyboink.entities.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VoteRepository : JpaRepository<Vote, Long> {
    fun findByPollPollId(pollId: String): List<Vote>
    fun findByPollPollIdAndUserUsername(pollId: String, username: String): Vote?
}
