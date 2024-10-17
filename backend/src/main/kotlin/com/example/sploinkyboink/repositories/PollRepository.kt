package com.example.sploinkyboink.repositories

import com.example.sploinkyboink.entities.Poll
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PollRepository : JpaRepository<Poll, String>
