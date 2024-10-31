package com.example.sploinkyboink

import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.repositories.UserRepository
import com.example.sploinkyboink.services.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootTest
class UserServiceTest {

    @MockBean
    private lateinit var userRepository: UserRepository

    private val userService: UserService by lazy {
        UserService(userRepository)
    }

    @Test
    fun `test register user success`() {
        val username = "testuser"
        val email = "testuser@example.com"
        val password = "Password123"
        val confirmPassword = "Password123"

        `when`(userRepository.findByUsername(username)).thenReturn(null)
        `when`(userRepository.findByEmail(email)).thenReturn(null)

        `when`(userRepository.save(any(User::class.java))).thenAnswer { invocation ->
            invocation.arguments[0] as User
        }

        val createdUser = userService.registerUser(username, password, confirmPassword, email)

        assertNotNull(createdUser)
        assertEquals(username, createdUser.username)
        assertEquals(email, createdUser.email)
        assertNotNull(createdUser.passwordHash)
        assertNotEquals(password, createdUser.passwordHash) // Password should be hashed

        verify(userRepository, times(1)).save(any(User::class.java))
    }

    @Test
    fun `test register user passwords do not match`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.registerUser("user", "pass1", "pass2", "email@example.com")
        }
        assertEquals("Passwords do not match", exception.message)
    }

    @Test
    fun `test register user username already taken`() {
        `when`(userRepository.findByUsername("existingUser")).thenReturn(User(1L, "existingUser", "email@example.com", "hash"))

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.registerUser("existingUser", "Password123", "Password123", "newemail@example.com")
        }
        assertEquals("Username existingUser is already taken", exception.message)
    }

    @Test
    fun `test authenticate with correct credentials`() {
        val rawPassword = "Password123"
        val hashedPassword = BCryptPasswordEncoder().encode(rawPassword)
        val user = User(1L, "user", "email@example.com", hashedPassword)

        `when`(userRepository.findByUsername("user")).thenReturn(user)

        val isAuthenticated = userService.authenticate("user", rawPassword)
        assertTrue(isAuthenticated)
    }

    @Test
    fun `test change email success`() {
        val userID = 1L
        val existingUser = User(userID, "user", "oldemail@example.com", "hash")

        `when`(userRepository.findById(userID)).thenReturn(java.util.Optional.of(existingUser))
        `when`(userRepository.findByEmail("newemail@example.com")).thenReturn(null)
        `when`(userRepository.save(existingUser)).thenReturn(existingUser)

        val updatedUser = userService.changeEmail(userID, "newemail@example.com")
        assertEquals("newemail@example.com", updatedUser.email)
    }

    @Test
    fun `test change email email already in use`() {
        `when`(userRepository.findByEmail("usedemail@example.com")).thenReturn(User(2L, "otheruser", "usedemail@example.com", "hash"))

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.changeEmail(1L, "usedemail@example.com")
        }
        assertEquals("Email usedemail@example.com is already in use", exception.message)
    }
}
