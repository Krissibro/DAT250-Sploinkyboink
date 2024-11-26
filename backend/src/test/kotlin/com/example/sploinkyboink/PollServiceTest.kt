// File: src/test/kotlin/com/example/sploinkyboink/PollServiceTest.kt

package com.example.sploinkyboink

import com.example.sploinkyboink.entities.Poll
import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.entities.Vote
import com.example.sploinkyboink.repositories.PollRepository
import com.example.sploinkyboink.repositories.VoteRepository
import com.example.sploinkyboink.services.EventService
import com.example.sploinkyboink.services.PollService
import com.example.sploinkyboink.services.UserService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class PollServiceTest {

    @Mock
    private lateinit var pollRepository: PollRepository

    @Mock
    private lateinit var voteRepository: VoteRepository

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var eventService: EventService

    @InjectMocks
    private lateinit var pollService: PollService

    private lateinit var user: User
    private lateinit var poll: Poll

    @BeforeEach
    fun setup() {
        user = User(
            userID = 1L, // Assigning non-null ID for tests
            username = "testuser",
            email = "test@example.com",
            passwordHash = "hashedpassword"
        )

        poll = Poll(
            pollID = "poll123",
            byUser = user,
            question = "Favorite color?",
            publishedAt = Instant.now(),
            lastModifiedAt = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            voteOptions = listOf("Red", "Blue")
        )
    }

    // Helper Methods
    private fun mockUserService(userID: Long, returnUser: User?) {
        `when`(userService.getUserByUserID(userID)).thenReturn(returnUser)
    }

    private fun mockPollRepositoryFindById(pollID: String, returnPoll: Poll?) {
        `when`(pollRepository.findById(pollID)).thenReturn(if (returnPoll != null) Optional.of(returnPoll) else Optional.empty())
    }

    private fun mockPollRepositorySave(returnPoll: Poll) {
        `when`(pollRepository.save(any())).thenReturn(returnPoll)
    }

    private fun mockVoteRepositorySave(returnVote: Vote) {
        `when`(voteRepository.save(any())).thenReturn(returnVote)
    }

    private fun verifyNoInteractionsWithVoteRepository() {
        verify(voteRepository, never()).save(any())
        verify(voteRepository, never()).delete(any())
    }

    // Nested Classes for Logical Grouping
    @Nested
    @DisplayName("Create Poll Tests")
    inner class CreatePollTests {

        @Test
        fun `should create poll successfully`() {
            // Arrange
            mockUserService(user.userID!!, user)
            val savedPoll = poll.copy(pollID = "poll123")
            mockPollRepositorySave(savedPoll)

            // Act
            val createdPoll = pollService.createPoll(user.userID!!, poll.question, poll.voteOptions, poll.validUntil)

            // Assert
            assertNotNull(createdPoll)
            assertEquals("poll123", createdPoll.pollID)
            assertEquals("Favorite color?", createdPoll.question)
            assertEquals(listOf("Red", "Blue"), createdPoll.voteOptions)

            // Verify interactions
            verify(userService, times(1)).getUserByUserID(user.userID!!)
            verify(pollRepository, times(1)).save(any())

            // Use Argument Captor to verify specific fields
            val pollCaptor = argumentCaptor<Poll>()
            verify(eventService, times(1)).sendPollCreatedEvent(pollCaptor.capture())
            val capturedPoll = pollCaptor.firstValue
            assertEquals("Favorite color?", capturedPoll.question)
            assertEquals(listOf("Red", "Blue"), capturedPoll.voteOptions)
            // You can add more assertions as needed
        }

        @Test
        fun `should throw exception when user does not exist`() {
            // Arrange
            mockUserService(user.userID!!, null)

            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                pollService.createPoll(user.userID!!, poll.question, poll.voteOptions, poll.validUntil)
            }
            assertEquals("User with userID ${user.userID} not found", exception.message)

            // Verify interactions
            verify(userService, times(1)).getUserByUserID(user.userID!!)
            verify(pollRepository, never()).save(any())
            verify(eventService, never()).sendPollCreatedEvent(any())
        }
    }

    @Nested
    @DisplayName("Vote on Poll Tests")
    inner class VoteOnPollTests {

        @Test
        fun `should vote on poll successfully`() {
            // Arrange
            mockPollRepositoryFindById(poll.pollID, poll)
            mockUserService(user.userID!!, user)
            val vote = Vote(
                id = 1L,
                poll = poll,
                user = user,
                voteOption = "Blue",
                publishedAt = Instant.now(),
                lastModifiedAt = Instant.now()
            )
            mockVoteRepositorySave(vote)

            // Act
            val createdVote = pollService.voteOnPoll(poll.pollID, user.userID!!, "Blue")

            // Assert
            assertNotNull(createdVote)
            assertEquals("Blue", createdVote.voteOption)
            assertEquals(poll, createdVote.poll)
            assertEquals(user, createdVote.user)

            // Verify interactions
            verify(pollRepository, times(1)).findById(poll.pollID)
            verify(userService, times(1)).getUserByUserID(user.userID!!)
            verify(voteRepository, times(1)).save(any())
            verify(eventService, times(1)).sendVoteEvent(poll)
        }

        @Test
        fun `should throw exception when poll does not exist`() {
            // Arrange
            mockPollRepositoryFindById("nonexistentpoll", null)

            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                pollService.voteOnPoll("nonexistentpoll", user.userID!!, "Blue")
            }
            assertEquals("Poll not found", exception.message)

            // Verify interactions
            verify(pollRepository, times(1)).findById("nonexistentpoll")
            verify(userService, never()).getUserByUserID(any())
            verify(voteRepository, never()).save(any())
            verify(eventService, never()).sendVoteEvent(any())
        }

        @Test
        fun `should throw exception when vote option is invalid`() {
            // Arrange
            mockPollRepositoryFindById(poll.pollID, poll)
            mockUserService(user.userID!!, user)

            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                pollService.voteOnPoll(poll.pollID, user.userID!!, "Green")
            }
            assertEquals("Invalid vote option", exception.message)

            // Verify interactions
            verify(pollRepository, times(1)).findById(poll.pollID)
            verify(userService, times(1)).getUserByUserID(user.userID!!)
            verify(voteRepository, never()).save(any())
            verify(eventService, never()).sendVoteEvent(any())
        }

        @Test
        fun `should throw exception when poll has expired`() {
            // Arrange
            val expiredPoll = poll.copy(validUntil = Instant.now().minusSeconds(3600))
            mockPollRepositoryFindById(expiredPoll.pollID, expiredPoll)
            mockUserService(user.userID!!, user)

            // Act & Assert
            val exception = assertThrows<IllegalStateException> {
                pollService.voteOnPoll(expiredPoll.pollID, user.userID!!, "Blue")
            }
            assertEquals("Poll has expired", exception.message)

            // Verify interactions
            verify(pollRepository, times(1)).findById(expiredPoll.pollID)
            verify(userService, times(1)).getUserByUserID(user.userID!!)
            verify(voteRepository, never()).save(any())
            verify(eventService, never()).sendVoteEvent(any())
        }

        @Test
        fun `should throw exception when user has already voted`() {
            // Arrange
            val existingVote = Vote(
                id = 1L,
                poll = poll,
                user = user,
                voteOption = "Red",
                publishedAt = Instant.now(),
                lastModifiedAt = Instant.now()
            )
            poll.votes.add(existingVote)
            mockPollRepositoryFindById(poll.pollID, poll)
            mockUserService(user.userID!!, user)

            // Act & Assert
            val exception = assertThrows<IllegalStateException> {
                pollService.voteOnPoll(poll.pollID, user.userID!!, "Blue")
            }
            assertEquals("User has already voted in this poll", exception.message)

            // Verify interactions
            verify(pollRepository, times(1)).findById(poll.pollID)
            verify(userService, times(1)).getUserByUserID(user.userID!!)
            verify(voteRepository, never()).save(any())
            verify(eventService, never()).sendVoteEvent(any())
        }

        @Test
        fun `should throw exception when user does not exist`() {
            // Arrange
            mockPollRepositoryFindById(poll.pollID, poll)
            mockUserService(user.userID!!, null)

            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                pollService.voteOnPoll(poll.pollID, user.userID!!, "Blue")
            }
            assertEquals("User with userID ${user.userID} not found", exception.message)

            // Verify interactions
            verify(pollRepository, times(1)).findById(poll.pollID)
            verify(userService, times(1)).getUserByUserID(user.userID!!)
            verify(voteRepository, never()).save(any())
            verify(eventService, never()).sendVoteEvent(any())
        }
    }

    @Nested
    @DisplayName("Poll Results Tests")
    inner class PollResultsTests {

        @Test
        fun `should get poll results successfully`() {
            // Arrange
            mockPollRepositoryFindById(poll.pollID, poll)
            val vote1 = Vote(
                id = 1L,
                poll = poll,
                user = User(2L, "user2", "email2@example.com", "hash"),
                voteOption = "Red",
                publishedAt = Instant.now(),
                lastModifiedAt = Instant.now()
            )
            val vote2 = Vote(
                id = 2L,
                poll = poll,
                user = User(3L, "user3", "email3@example.com", "hash"),
                voteOption = "Blue",
                publishedAt = Instant.now(),
                lastModifiedAt = Instant.now()
            )
            poll.votes.addAll(listOf(vote1, vote2))

            // Act
            val results = pollService.getPollResults(poll.pollID)

            // Assert
            assertNotNull(results)
            assertEquals(2, results.size)
            assertEquals(1L, results["Red"])
            assertEquals(1L, results["Blue"])
            verify(pollRepository, times(1)).findById(poll.pollID)
        }

        @Test
        fun `should throw exception when getting results for non-existent poll`() {
            // Arrange
            mockPollRepositoryFindById("nonexistentpoll", null)

            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                pollService.getPollResults("nonexistentpoll")
            }
            assertEquals("Poll with ID nonexistentpoll not found", exception.message)

            // Verify interactions
            verify(pollRepository, times(1)).findById("nonexistentpoll")
        }
    }

    @Nested
    @DisplayName("Edit Poll Tests")
    inner class EditPollTests {

        @Test
        fun `should edit poll successfully`() {
            // Arrange
            val updatedQuestion = "Updated Question?"
            val updatedOptions = listOf("Green", "Yellow")
            val updatedValidUntil = Instant.now().plusSeconds(7200)
            val existingVote = Vote(
                id = 1L,
                poll = poll,
                user = user,
                voteOption = "Red",
                publishedAt = Instant.now(),
                lastModifiedAt = Instant.now()
            )
            poll.votes.add(existingVote)
            mockPollRepositoryFindById(poll.pollID!!, poll)

            val updatedPoll = poll.copy(
                question = updatedQuestion,
                voteOptions = updatedOptions,
                validUntil = updatedValidUntil,
                lastModifiedAt = Instant.now().plusSeconds(1800)
            )
            mockPollRepositorySave(updatedPoll)

            // Act
            val result = pollService.editPoll(poll.pollID!!, updatedQuestion, updatedOptions, updatedValidUntil)

            // Assert
            assertNotNull(result)
            assertEquals(updatedQuestion, result.question)
            assertEquals(updatedOptions, result.voteOptions)
            assertEquals(updatedValidUntil, result.validUntil)
            assertEquals(0, result.votes.size) // Votes for removed options should be deleted

            // Verify interactions
            verify(pollRepository, times(1)).findById(poll.pollID!!)
            verify(pollRepository, times(1)).save(any())
            verify(eventService, times(1)).sendPollEditedEvent(result)
        }

        @Test
        fun `should throw exception when editing non-existent poll`() {
            // Arrange
            mockPollRepositoryFindById("nonexistentpoll", null)
            val updatedQuestion = "Updated Question?"
            val updatedOptions = listOf("Green", "Yellow")
            val updatedValidUntil = Instant.now().plusSeconds(7200)

            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                pollService.editPoll("nonexistentpoll", updatedQuestion, updatedOptions, updatedValidUntil)
            }
            assertEquals("Poll not found", exception.message)

            // Verify interactions
            verify(pollRepository, times(1)).findById("nonexistentpoll")
            verify(pollRepository, never()).save(any())
            verify(eventService, never()).sendPollEditedEvent(any())
        }
    }

    @Nested
    @DisplayName("Delete Poll Tests")
    inner class DeletePollTests {

        @Test
        fun `should delete poll successfully`() {
            // Arrange
            mockPollRepositoryFindById(poll.pollID!!, poll)
            doNothing().`when`(pollRepository).deleteById(poll.pollID!!)

            // Act
            pollService.deletePoll(poll.pollID!!)

            // Assert
            verify(pollRepository, times(1)).findById(poll.pollID!!)
            verify(pollRepository, times(1)).deleteById(poll.pollID!!)
            // If your service sends an event on deletion, verify it here
        }

        @Test
        fun `should throw exception when deleting non-existent poll`() {
            // Arrange
            mockPollRepositoryFindById("nonexistentpoll", null)

            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                pollService.deletePoll("nonexistentpoll")
            }
            assertEquals("Poll not found", exception.message)

            // Verify interactions
            verify(pollRepository, times(1)).findById("nonexistentpoll")
            verify(pollRepository, never()).deleteById(any())
        }
    }

    @Nested
    @DisplayName("Delete Vote Tests")
    inner class DeleteVoteTests {

        @Test
        fun `should delete vote successfully`() {
            // Arrange
            val userID = user.userID!!
            val existingVote = Vote(
                id = 1L,
                poll = poll,
                user = user,
                voteOption = "Red",
                publishedAt = Instant.now(),
                lastModifiedAt = Instant.now()
            )
            poll.votes.add(existingVote)
            mockPollRepositoryFindById(poll.pollID!!, poll)
            mockUserService(userID, user)
            doNothing().`when`(voteRepository).delete(existingVote)

            // Act
            pollService.deleteVote(poll.pollID!!, userID)

            // Assert
            assertFalse(poll.votes.contains(existingVote))
            verify(pollRepository, times(1)).findById(poll.pollID!!)
            verify(userService, times(1)).getUserByUserID(userID)
            verify(voteRepository, times(1)).delete(existingVote)
            verify(eventService, times(1)).sendVoteEvent(poll)
        }

        @Test
        fun `should throw exception when deleting non-existent vote`() {
            // Arrange
            val userID = user.userID!!
            mockPollRepositoryFindById(poll.pollID!!, poll)
            mockUserService(userID, user)

            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                pollService.deleteVote(poll.pollID!!, userID)
            }
            assertEquals("No existing vote to delete", exception.message)

            // Verify interactions
            verify(pollRepository, times(1)).findById(poll.pollID!!)
            verify(userService, times(1)).getUserByUserID(userID)
            verify(voteRepository, never()).delete(any())
            verify(eventService, never()).sendVoteEvent(any())
        }
    }
}
