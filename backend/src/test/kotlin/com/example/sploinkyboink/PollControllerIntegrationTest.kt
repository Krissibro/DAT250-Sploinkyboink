import com.example.sploinkyboink.SploinkyboinkApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.MvcResult

@SpringBootTest(classes = [SploinkyboinkApplication::class])
@AutoConfigureMockMvc
class PollControllerMockMvcTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `test poll app flow with MockMvc`() {
        // 1. Create a new user (User 1)
        mockMvc.perform(post("/api/users")
            .param("username", "john_doe")
            .param("email", "john@example.com"))
            .andExpect(status().isCreated)
            .andExpect(content().string("User created"))

        // 2. List all users (should show User 1)
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].username").value("john_doe"))

        // 3. Create another user (User 2)
        mockMvc.perform(post("/api/users")
            .param("username", "jane_doe")
            .param("email", "jane@example.com"))
            .andExpect(status().isCreated)
            .andExpect(content().string("User created"))

        // 4. List all users again (should show both users)
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[1].username").value("jane_doe"))

        // 5. User 1 creates a new poll and capture the pollId
        val result: MvcResult = mockMvc.perform(post("/api/polls")
            .param("username", "john_doe")
            .param("question", "What's your favorite color?")
            .param("voteOptions", "Red", "Blue", "Green"))
            .andExpect(status().isCreated)
            .andReturn()

        val responseContent = result.response.contentAsString
        val pollId = responseContent.substringAfter("Poll created with ID: ")

        // 6. List polls (should show the newly created poll)
        mockMvc.perform(get("/api/polls"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].pollId").value(pollId))

        // 7. User 2 votes on the poll
        mockMvc.perform(post("/api/polls/{pollId}/vote", pollId)
            .param("username", "jane_doe")
            .param("voteOption", "Red"))
            .andExpect(status().isOk)
            .andExpect(content().string("Vote registered"))

        // 8. User 2 changes their vote
        mockMvc.perform(put("/api/polls/{pollId}/vote", pollId)
            .param("username", "jane_doe")
            .param("voteOption", "Blue"))
            .andExpect(status().isOk)
            .andExpect(content().string("Vote updated"))

        // 9. List votes (should show the most recent vote for User 2)
        mockMvc.perform(get("/api/polls/{pollId}/votes", pollId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$['jane_doe'].voteOption").value("Blue"))

        // 10. Delete the poll
        mockMvc.perform(delete("/api/polls/{pollId}", pollId))
            .andExpect(status().isOk)
            .andExpect(content().string("Poll deleted"))

        // 11. List votes (should be empty after the poll is deleted)
        mockMvc.perform(get("/api/polls/{pollId}/votes", pollId))
            .andExpect(status().isNotFound)
    }
}
