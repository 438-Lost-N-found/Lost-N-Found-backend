package com.lostnfound.api.auth

import com.lostnfound.api.user.User
import com.lostnfound.api.user.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import java.util.UUID

class AuthServiceTest {

    private val supabaseAuthClient = mock(SupabaseAuthClient::class.java)
    private val userRepository = mock(UserRepository::class.java)

    private val authService = AuthService(
        supabaseAuthClient,
        userRepository
    )

    @Test
    fun `register creates a new user successfully`() {
        val authUserId = UUID.randomUUID()

        val savedUser = User(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            displayName = "testuser",
            authUserId = authUserId
        )

        `when`(userRepository.findByUsername("testuser"))
            .thenReturn(null, savedUser)

        `when`(
            supabaseAuthClient.register(
                "testuser",
                "test@example.com",
                "password123"
            )
        ).thenReturn(
            mapOf("user" to mapOf("id" to authUserId.toString()))
        )

        val result = authService.register(
            "testuser",
            "test@example.com",
            "password123"
        )

        assertEquals(savedUser, result)

        verify(userRepository).createUser(
            "testuser",
            "test@example.com",
            "testuser",
            authUserId
        )
    }

    @Test
    fun `register rejects an existing username`() {
        val existingUser = User(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            displayName = "testuser",
            authUserId = UUID.randomUUID()
        )

        `when`(userRepository.findByUsername("testuser"))
            .thenReturn(existingUser)

        assertThrows(IllegalArgumentException::class.java) {
            authService.register(
                "testuser",
                "new@example.com",
                "password123"
            )
        }

        verify(supabaseAuthClient, never()).register(
            "testuser",
            "new@example.com",
            "password123"
        )
    }

    @Test
    fun `login succeeds for an existing user`() {
        val existingUser = User(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            displayName = "testuser",
            authUserId = UUID.randomUUID()
        )

        val loginResponse = mapOf<String, Any>(
            "access_token" to "fake-token"
        )

        `when`(userRepository.findByUsername("testuser"))
            .thenReturn(existingUser)

        `when`(
            supabaseAuthClient.login(
                "test@example.com",
                "password123"
            )
        ).thenReturn(loginResponse)

        val result = authService.login("testuser", "password123")

        assertEquals(loginResponse, result)
    }

    @Test
    fun `login rejects an unknown username`() {
        `when`(userRepository.findByUsername("unknown"))
            .thenReturn(null)

        assertThrows(IllegalArgumentException::class.java) {
            authService.login("unknown", "password123")
        }

        verify(supabaseAuthClient, never()).login(
            "unknown@example.com",
            "password123"
        )
    }

    @Test
    fun `register trims username and email`() {
        val authUserId = UUID.randomUUID()

        val savedUser = User(
            id = 2L,
            username = "testuser",
            email = "test@example.com",
            displayName = "testuser",
            authUserId = authUserId
        )

        `when`(userRepository.findByUsername("testuser"))
            .thenReturn(null, savedUser)

        `when`(
            supabaseAuthClient.register(
                "testuser",
                "test@example.com",
                "password123"
            )
        ).thenReturn(
            mapOf("user" to mapOf("id" to authUserId.toString()))
        )

        val result = authService.register(
            " testuser ",
            " test@example.com ",
            "password123"
        )

        assertEquals(savedUser, result)

        verify(supabaseAuthClient).register(
            "testuser",
            "test@example.com",
            "password123"
        )
    }

    @Test
    fun `register fails when Supabase does not return user information`() {
        `when`(userRepository.findByUsername("testuser"))
            .thenReturn(null)

        `when`(
            supabaseAuthClient.register(
                "testuser",
                "test@example.com",
                "password123"
            )
        ).thenReturn(emptyMap())

        assertThrows(IllegalStateException::class.java) {
            authService.register(
                "testuser",
                "test@example.com",
                "password123"
            )
        }
    }

    @Test
    fun `register fails when Supabase does not return a user ID`() {
        `when`(userRepository.findByUsername("testuser"))
            .thenReturn(null)

        `when`(
            supabaseAuthClient.register(
                "testuser",
                "test@example.com",
                "password123"
            )
        ).thenReturn(
            mapOf("user" to emptyMap<String, Any>())
        )

        assertThrows(IllegalStateException::class.java) {
            authService.register(
                "testuser",
                "test@example.com",
                "password123"
            )
        }
    }

}
