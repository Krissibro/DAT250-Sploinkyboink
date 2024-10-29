package com.example.sploinkyboink.repositories

import com.example.sploinkyboink.entities.Event
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : MongoRepository<Event, String>