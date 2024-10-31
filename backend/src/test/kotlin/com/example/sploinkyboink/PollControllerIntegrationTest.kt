package com.example.sploinkyboink

import com.example.sploinkyboink.SploinkyboinkApplication
import com.example.sploinkyboink.entities.Poll
import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.repositories.PollRepository
import com.example.sploinkyboink.repositories.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Instant

@SpringBootTest(classes = [SploinkyboinkApplication::class])
@AutoConfigureMockMvc
class PollControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var pollRepository: PollRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var user1: User
    private lateinit var user2: User

    @BeforeEach
    fun setup() {
        // Clear repositories before each test
        pollRepository.deleteAll()
        userRepository.deleteAll()

        // Create User 1
        user1 = User(
            userID = 1L,
            username = "john_doe",
            email = "john@example.com",
            passwordHash = "hashedpassword1"
        )
        userRepository.save(user1)

        // Create User 2
        user2 = User(
            userID = 2L,
            username = "jane_doe",
            email = "jane@example.com",
            passwordHash = "hashedpassword2"
        )
        userRepository.save(user2)
    }

    @Test
    fun `Create, retrieve, vote, edit vote, delete vote, edit poll, delete poll, and fetch results`() {
        // 1. Create a new poll
        val validUntil = Instant.now().plusSeconds(3600).toString() // 1 hour from now
        val createPollResult: MvcResult = mockMvc.perform(
            post("/sploinkyboinkend/polls")
                .param("userID", user1.userID.toString())
                .param("question", "What's your favorite programming language?")
                .param("voteOptions", "Kotlin", "Java", "Python")
                .param("validUntil", validUntil)
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(containsString("Poll created with ID:")))
            .andReturn()

        // Extract pollID from response
        val createPollResponse = createPollResult.response.contentAsString
        val pollId = createPollResponse.substringAfter("Poll created with ID: ").trim()

        assertTrue(pollId.isNotEmpty(), "Poll ID should not be empty")

        // 2. Retrieve all polls and verify the created poll is present
        mockMvc.perform(get("/sploinkyboinkend/polls"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content", hasSize<Any>(1)))
            .andExpect(jsonPath("$.content[0].pollID", `is`(pollId)))
            .andExpect(jsonPath("$.content[0].question", `is`("What's your favorite programming language?")))
            .andExpect(jsonPath("$.content[0].voteOptions", containsInAnyOrder("Kotlin", "Java", "Python")))

        // 3. Retrieve active polls and verify the poll is active
        mockMvc.perform(get("/sploinkyboinkend/polls/active"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content", hasSize<Any>(1)))
            .andExpect(jsonPath("$.content[0].pollID", `is`(pollId)))

        // 4. User 2 votes on the poll
        mockMvc.perform(
            post("/sploinkyboinkend/polls/$pollId/vote")
                .param("userID", user2.userID.toString())
                .param("voteOption", "Java")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Vote registered successfully"))

        // 5. Verify that the vote is registered by fetching poll results
        mockMvc.perform(get("/sploinkyboinkend/polls/$pollId/results"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.Java", `is`(1)))
            .andExpect(jsonPath("$.Kotlin", `is`(0)))
            .andExpect(jsonPath("$.Python", `is`(0)))

        // 6. User 2 edits their vote to "Python"
        mockMvc.perform(
            put("/sploinkyboinkend/polls/$pollId/vote")
                .param("userID", user2.userID.toString())
                .param("newVoteOption", "Python")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Vote updated successfully"))

        // 7. Verify that the vote has been updated
        mockMvc.perform(get("/sploinkyboinkend/polls/$pollId/results"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.Java", `is`(0)))
            .andExpect(jsonPath("$.Kotlin", `is`(0)))
            .andExpect(jsonPath("$.Python", `is`(1)))

        // 8. User 2 deletes their vote
        mockMvc.perform(
            delete("/sploinkyboinkend/polls/$pollId/vote")
                .param("userID", user2.userID.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Vote deleted"))

        // 9. Verify that the vote has been deleted
        mockMvc.perform(get("/sploinkyboinkend/polls/$pollId/results"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.Java").doesNotExist())
            .andExpect(jsonPath("$.Kotlin").doesNotExist())
            .andExpect(jsonPath("$.Python").doesNotExist())

        // 10. Edit the poll's question and vote options
        val newValidUntil = Instant.now().plusSeconds(7200).toString() // 2 hours from now
        mockMvc.perform(
            put("/sploinkyboinkend/polls/$pollId")
                .param("question", "What's your preferred IDE?")
                .param("voteOptions", "IntelliJ IDEA", "Eclipse", "VS Code")
                .param("validUntil", newValidUntil)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Poll updated successfully"))

        // 11. Retrieve the updated poll and verify changes
        mockMvc.perform(get("/sploinkyboinkend/polls"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content", hasSize<Any>(1)))
            .andExpect(jsonPath("$.content[0].question", `is`("What's your preferred IDE?")))
            .andExpect(jsonPath("$.content[0].voteOptions", containsInAnyOrder("IntelliJ IDEA", "Eclipse", "VS Code")))

        // 12. Delete the poll
        mockMvc.perform(delete("/sploinkyboinkend/polls/$pollId"))
            .andExpect(status().isOk)
            .andExpect(content().string("Poll deleted"))

        // 13. Verify that the poll has been deleted
        mockMvc.perform(get("/sploinkyboinkend/polls"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content", hasSize<Any>(0)))

        // 14. Attempt to fetch results of the deleted poll
        mockMvc.perform(get("/sploinkyboinkend/polls/$pollId/results"))
            .andExpect(status().isNotFound)
            .andExpect(content().string(containsString("Poll with ID $pollId not found")))
    }

    @Test
    fun `Attempt to vote on non-existent poll`() {
        val nonExistentPollId = "nonexistentpoll123"

        mockMvc.perform(
            post("/sploinkyboinkend/polls/$nonExistentPollId/vote")
                .param("userID", user1.userID.toString())
                .param("voteOption", "Option1")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Poll not found"))
    }

    @Test
    fun `Attempt to create poll with invalid vote option`() {
        val validUntil = Instant.now().plusSeconds(3600).toString()
        mockMvc.perform(
            post("/sploinkyboinkend/polls")
                .param("userID", user1.userID.toString())
                .param("question", "Invalid vote option test")
                .param("voteOptions", "Option1", "Option2")
                .param("validUntil", validUntil)
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(containsString("Poll created with ID:")))
            .andReturn()

        // Extract pollID
        val createPollResponse = mockMvc.perform(
            post("/sploinkyboinkend/polls")
                .param("userID", user1.userID.toString())
                .param("question", "Another poll")
                .param("voteOptions", "OptionA", "OptionB")
                .param("validUntil", validUntil)
        ).andReturn().response.contentAsString

        val pollId = createPollResponse.substringAfter("Poll created with ID: ").trim()

        // Attempt to vote with an invalid option
        mockMvc.perform(
            post("/sploinkyboinkend/polls/$pollId/vote")
                .param("userID", user2.userID.toString())
                .param("voteOption", "InvalidOption")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Invalid vote option"))
    }

    @Test
    fun `Attempt to create poll with past validUntil`() {
        val pastValidUntil = Instant.now().minusSeconds(3600).toString() // 1 hour ago
        mockMvc.perform(
            post("/sploinkyboinkend/polls")
                .param("userID", user1.userID.toString())
                .param("question", "Poll with past validUntil")
                .param("voteOptions", "Yes", "No")
                .param("validUntil", pastValidUntil)
        )
            .andExpect(status().isCreated) // Depending on business logic, this might be allowed
            .andExpect(content().string(containsString("Poll created with ID:")))
            .andReturn()
    }

    @Test
    fun `Attempt to vote on expired poll`() {
        // Create a poll that has already expired
        val expiredValidUntil = Instant.now().minusSeconds(3600).toString() // 1 hour ago
        val createPollResult: MvcResult = mockMvc.perform(
            post("/sploinkyboinkend/polls")
                .param("userID", user1.userID.toString())
                .param("question", "Expired poll test")
                .param("voteOptions", "Option1", "Option2")
                .param("validUntil", expiredValidUntil)
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(containsString("Poll created with ID:")))
            .andReturn()

        // Extract pollID
        val pollId = createPollResult.response.contentAsString.substringAfter("Poll created with ID: ").trim()

        // Attempt to vote on the expired poll
        mockMvc.perform(
            post("/sploinkyboinkend/polls/$pollId/vote")
                .param("userID", user2.userID.toString())
                .param("voteOption", "Option1")
        )
            .andExpect(status().isConflict)
            .andExpect(content().string("Poll has expired"))
    }

    @Test
    fun `Attempt to create poll with non-existent user`() {
        val nonExistentUserId = 999L
        val validUntil = Instant.now().plusSeconds(3600).toString()

        mockMvc.perform(
            post("/sploinkyboinkend/polls")
                .param("userID", nonExistentUserId.toString())
                .param("question", "Poll with invalid user")
                .param("voteOptions", "Option1", "Option2")
                .param("validUntil", validUntil)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("User with userID $nonExistentUserId not found"))
    }

    @Test
    fun `Attempt to vote multiple times by the same user`() {
        // Create a poll
        val validUntil = Instant.now().plusSeconds(3600).toString()
        val createPollResult: MvcResult = mockMvc.perform(
            post("/sploinkyboinkend/polls")
                .param("userID", user1.userID.toString())
                .param("question", "Multiple voting test")
                .param("voteOptions", "Option1", "Option2")
                .param("validUntil", validUntil)
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(containsString("Poll created with ID:")))
            .andReturn()

        // Extract pollID
        val pollId = createPollResult.response.contentAsString.substringAfter("Poll created with ID: ").trim()

        // First vote
        mockMvc.perform(
            post("/sploinkyboinkend/polls/$pollId/vote")
                .param("userID", user2.userID.toString())
                .param("voteOption", "Option1")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Vote registered successfully"))

        // Attempt to vote again by the same user
        mockMvc.perform(
            post("/sploinkyboinkend/polls/$pollId/vote")
                .param("userID", user2.userID.toString())
                .param("voteOption", "Option2")
        )
            .andExpect(status().isConflict)
            .andExpect(content().string("User has already voted in this poll"))
    }
}
