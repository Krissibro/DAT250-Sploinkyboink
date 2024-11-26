// File: src/test/kotlin/com/example/sploinkyboink/UserServiceTest.kt

package com.example.sploinkyboink

import com.example.sploinkyboink.entities.User
import com.example.sploinkyboink.repositories.UserRepository
import com.example.sploinkyboink.services.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    private val passwordEncoder = BCryptPasswordEncoder()

    @BeforeEach
    fun setUp() {
        // No setup needed as we are using @InjectMocks and @Mock
    }

    @Test
    fun `test register user success`() {
        val username = "testuser"
        val email = "testuser@example.com"
        val password = "Password123"
        val confirmPassword = "Password123"

        // Mock repository behavior
        `when`(userRepository.findByUsername(username)).thenReturn(null)
        `when`(userRepository.findByEmail(email)).thenReturn(null)
        `when`(userRepository.save(any(User::class.java))).thenAnswer { invocation ->
            val user = invocation.arguments[0] as User
            user.copy(userID = 1L) // Simulate saved user with ID
        }

        val createdUser = userService.registerUser(username, password, confirmPassword, email)

        // Assertions
        assertNotNull(createdUser)
        assertEquals(1L, createdUser.userID)
        assertEquals(username, createdUser.username)
        assertEquals(email, createdUser.email)
        assertNotNull(createdUser.passwordHash)
        assertNotEquals(password, createdUser.passwordHash) // Password should be hashed
        assertTrue(passwordEncoder.matches(password, createdUser.passwordHash))

        // Verify interactions
        verify(userRepository, times(1)).findByUsername(username)
        verify(userRepository, times(1)).findByEmail(email)
        verify(userRepository, times(1)).save(any(User::class.java))
    }

    @Test
    fun `test register user passwords do not match`() {
        val username = "user"
        val email = "user@example.com"
        val password = "Password123"
        val confirmPassword = "Password456"

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.registerUser(username, password, confirmPassword, email)
        }

        assertEquals("Passwords do not match", exception.message)

        // Verify that repository methods were never called
        verify(userRepository, never()).findByUsername(anyString())
        verify(userRepository, never()).findByEmail(anyString())
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun `test register user username already taken`() {
        val username = "existingUser"
        val email = "newemail@example.com"
        val password = "Password123"
        val confirmPassword = "Password123"

        // Mock repository behavior
        val existingUser = User(userID = 1L, username = username, email = "existing@example.com", passwordHash = "hash")
        `when`(userRepository.findByUsername(username)).thenReturn(existingUser)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.registerUser(username, password, confirmPassword, email)
        }

        assertEquals("Username existingUser is already taken", exception.message)

        // Verify interactions
        verify(userRepository, times(1)).findByUsername(username)
        verify(userRepository, never()).findByEmail(anyString())
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun `test register user email already in use`() {
        val username = "newUser"
        val email = "usedemail@example.com"
        val password = "Password123"
        val confirmPassword = "Password123"

        // Mock repository behavior
        `when`(userRepository.findByUsername(username)).thenReturn(null)
        val existingUser = User(userID = 2L, username = "otherUser", email = email, passwordHash = "hash")
        `when`(userRepository.findByEmail(email)).thenReturn(existingUser)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.registerUser(username, password, confirmPassword, email)
        }

        assertEquals("Email usedemail@example.com is already in use", exception.message)

        // Verify interactions
        verify(userRepository, times(1)).findByUsername(username)
        verify(userRepository, times(1)).findByEmail(email)
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun `test authenticate with correct username and password`() {
        val username = "validUser"
        val rawPassword = "Password123"
        val hashedPassword = passwordEncoder.encode(rawPassword)
        val user = User(userID = 1L, username = username, email = "user@example.com", passwordHash = hashedPassword)

        // Mock repository behavior
        `when`(userRepository.findByUsername(username)).thenReturn(user)

        val isAuthenticated = userService.authenticate(username, rawPassword)

        assertTrue(isAuthenticated)

        // Verify interactions
        verify(userRepository, times(1)).findByUsername(username)
    }

    @Test
    fun `test authenticate with incorrect password`() {
        val username = "validUser"
        val rawPassword = "Password123"
        val wrongPassword = "WrongPassword"
        val hashedPassword = passwordEncoder.encode(rawPassword)
        val user = User(userID = 1L, username = username, email = "user@example.com", passwordHash = hashedPassword)

        // Mock repository behavior
        `when`(userRepository.findByUsername(username)).thenReturn(user)

        val isAuthenticated = userService.authenticate(username, wrongPassword)

        assertFalse(isAuthenticated)

        // Verify interactions
        verify(userRepository, times(1)).findByUsername(username)
    }

    @Test
    fun `test authenticate with non-existent user`() {
        val username = "nonExistentUser"
        val rawPassword = "Password123"

        // Mock repository behavior
        `when`(userRepository.findByUsername(username)).thenReturn(null)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.authenticate(username, rawPassword)
        }

        assertEquals("User with username nonExistentUser not found", exception.message)

        // Verify interactions
        verify(userRepository, times(1)).findByUsername(username)
    }

    @Test
    fun `test change email success`() {
        val userID = 1L
        val newEmail = "newemail@example.com"
        val existingUser = User(userID = userID, username = "user", email = "oldemail@example.com", passwordHash = "hash")

        // Mock repository behavior
        `when`(userRepository.findById(userID)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.findByEmail(newEmail)).thenReturn(null)
        `when`(userRepository.save(existingUser)).thenReturn(existingUser.copy(email = newEmail))

        val updatedUser = userService.changeEmail(userID, newEmail)

        assertNotNull(updatedUser)
        assertEquals(newEmail, updatedUser.email)

        // Verify interactions
        verify(userRepository, times(1)).findById(userID)
        verify(userRepository, times(1)).findByEmail(newEmail)
        verify(userRepository, times(1)).save(existingUser)
    }

    @Test
    fun `test change email email already in use`() {
        val userID = 1L
        val newEmail = "usedemail@example.com"
        val existingUser = User(userID = userID, username = "user", email = "oldemail@example.com", passwordHash = "hash")
        val anotherUser = User(userID = 2L, username = "otherUser", email = newEmail, passwordHash = "hash")

        // Mock repository behavior
        `when`(userRepository.findById(userID)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.findByEmail(newEmail)).thenReturn(anotherUser)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.changeEmail(userID, newEmail)
        }

        assertEquals("Email usedemail@example.com is already in use", exception.message)

        // Verify interactions
        verify(userRepository, times(1)).findById(userID)
        verify(userRepository, times(1)).findByEmail(newEmail)
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun `test change email for non-existent user`() {
        val userID = 999L
        val newEmail = "newemail@example.com"

        // Mock repository behavior
        `when`(userRepository.findById(userID)).thenReturn(Optional.empty())

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.changeEmail(userID, newEmail)
        }

        assertEquals("User with userID $userID not found", exception.message)

        // Verify interactions
        verify(userRepository, times(1)).findById(userID)
        verify(userRepository, never()).findByEmail(anyString())
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun `test change password success`() {
        val userID = 1L
        val newPassword = "NewPassword123"
        val confirmPassword = "NewPassword123"
        val existingUser = User(userID = userID, username = "user", email = "user@example.com", passwordHash = "oldHash")

        // Mock repository behavior
        `when`(userRepository.findById(userID)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.save(existingUser)).thenAnswer { invocation ->
            invocation.arguments[0] as User
        }

        val updatedUser = userService.changePassword(userID, newPassword, confirmPassword)

        assertNotNull(updatedUser)
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.passwordHash))

        // Verify interactions
        verify(userRepository, times(1)).findById(userID)
        verify(userRepository, times(1)).save(existingUser)
    }

    @Test
    fun `test change password passwords do not match`() {
        val userID = 1L
        val newPassword = "NewPassword123"
        val confirmPassword = "DifferentPassword123"

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.changePassword(userID, newPassword, confirmPassword)
        }

        assertEquals("Passwords do not match", exception.message)

        // Verify interactions
        verify(userRepository, never()).findById(anyLong())
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun `test change password weak password`() {
        val userID = 1L
        val newPassword = "short"
        val confirmPassword = "short"
        val existingUser = User(userID = userID, username = "user", email = "user@example.com", passwordHash = "oldHash")

        // Mock repository behavior
        // Even though password is weak, the service should still check userRepository.findById after password validation fails
        // But based on the service code, it does not proceed to findById if password is invalid
        // Hence, findById should NOT be called

        // No need to mock findById because it shouldn't be called

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.changePassword(userID, newPassword, confirmPassword)
        }

        assertEquals("Password must be at least 8 characters long", exception.message)

        // Verify interactions
        verify(userRepository, never()).findById(anyLong())
        verify(userRepository, never()).save(any(User::class.java))
    }

    // Add more tests as needed for other UserService methods
}
