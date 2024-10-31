package com.example.sploinkyboink

import com.example.sploinkyboink.entities.Poll
import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.entities.Vote
import com.example.sploinkyboink.repositories.PollRepository
import com.example.sploinkyboink.repositories.VoteRepository
import com.example.sploinkyboink.services.EventService
import com.example.sploinkyboink.services.PollService
import com.example.sploinkyboink.services.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.time.Instant

class PollServiceTest {

    private val pollRepository = mock(PollRepository::class.java)
    private val voteRepository = mock(VoteRepository::class.java)
    private val userService = mock(UserService::class.java)
    private val eventService = mock(EventService::class.java)

    private val pollService = PollService(pollRepository, voteRepository, userService, eventService)

    @Test
    fun `test create poll success`() {
        val userID = 1L
        val user = User(userID, "testuser", "test@example.com", "hash")
        val question = "Favorite color?"
        val options = listOf("Red", "Blue")
        val validUntil = Instant.now().plusSeconds(3600)

        `when`(userService.getUserByUserID(userID)).thenReturn(user)
        `when`(pollRepository.save(any(Poll::class.java))).thenAnswer { it.arguments[0] }

        val poll = pollService.createPoll(userID, question, options, validUntil)

        assertNotNull(poll)
        assertEquals(question, poll.question)
        assertEquals(options, poll.voteOptions)
        verify(eventService, times(1)).sendPollCreatedEvent(poll)
    }

    @Test
    fun `test vote on poll success`() {
        val pollID = "poll123"
        val userID = 1L
        val voteOption = "Blue"
        val user = User(userID, "user", "email@example.com", "hash")
        val poll = Poll(pollID, user, "Question?", Instant.now(), Instant.now(), Instant.now().plusSeconds(3600), listOf("Red", "Blue"))

        `when`(pollRepository.findById(pollID)).thenReturn(java.util.Optional.of(poll))
        `when`(userService.getUserByUserID(userID)).thenReturn(user)
        `when`(voteRepository.save(any(Vote::class.java))).thenAnswer { it.arguments[0] }

        val vote = pollService.voteOnPoll(pollID, userID, voteOption)

        assertNotNull(vote)
        assertEquals(voteOption, vote.voteOption)
        verify(eventService, times(1)).sendVoteEvent(poll)
    }

    @Test
    fun `test get poll results`() {
        val pollID = "poll123"
        val poll = Poll(pollID, null, "Question?", Instant.now(), Instant.now(), Instant.now().plusSeconds(3600), listOf("Red", "Blue"))
        val vote1 = Vote(1L, poll, User(1L, "user1", "email1", "hash"), "Red", Instant.now(), Instant.now())
        val vote2 = Vote(2L, poll, User(2L, "user2", "email2", "hash"), "Blue", Instant.now(), Instant.now())
        poll.votes.addAll(setOf(vote1, vote2))

        `when`(pollRepository.findById(pollID)).thenReturn(java.util.Optional.of(poll))

        val results = pollService.getPollResults(pollID)

        assertEquals(2, results.size)
        assertEquals(1L, results["Red"])
        assertEquals(1L, results["Blue"])
    }
}
