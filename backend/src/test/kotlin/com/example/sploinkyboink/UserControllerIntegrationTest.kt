package com.example.sploinkyboink

import com.example.sploinkyboink.SploinkyboinkApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest(classes = [SploinkyboinkApplication::class])
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `test user registration and retrieval`() {
        // 1. Register a new user
        mockMvc.perform(post("/sploinkyboinkend/users")
            .param("username", "testuser")
            .param("password", "Password123")
            .param("confirmPassword", "Password123")
            .param("email", "testuser@example.com"))
            .andExpect(status().isCreated)
            .andExpect(content().string("User created"))

        // 2. Get all users and retrieve the user ID
        val result = mockMvc.perform(get("/sploinkyboinkend/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].username").value("testuser"))
            .andReturn()

        val responseContent = result.response.contentAsString
        val userIdRegex = """"userID":(\d+)""".toRegex()
        val matchResult = userIdRegex.find(responseContent)
        val userID = matchResult?.groupValues?.get(1)?.toLong() ?: throw Exception("User ID not found")

        // 3. Change user's email
        mockMvc.perform(put("/sploinkyboinkend/users/{userID}/email", userID)
            .param("newEmail", "newemail@example.com"))
            .andExpect(status().isOk)
            .andExpect(content().string("Email updated successfully"))

        // 4. Get user by username to verify email change
        mockMvc.perform(get("/sploinkyboinkend/users/{username}", "testuser"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("newemail@example.com"))

        // 5. Delete the user
        mockMvc.perform(delete("/sploinkyboinkend/users/{userID}", userID))
            .andExpect(status().isOk)
            .andExpect(content().string("User deleted"))

        // 6. Attempt to get deleted user
        mockMvc.perform(get("/sploinkyboinkend/users/{username}", "testuser"))
            .andExpect(status().isNotFound)
    }
}
