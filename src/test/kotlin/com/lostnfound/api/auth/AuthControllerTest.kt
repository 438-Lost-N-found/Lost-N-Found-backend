
package com.lostnfound.api.auth

import com.lostnfound.api.user.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import java.util.UUID

class AuthControllerTest {

    private val authService = mock(AuthService::class.java)

    private val authController = AuthController(authService)

    @Test
    fun `register creates a new user`() {
        val expectedUser = User(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            displayName = "Test User",
            authUserId = UUID.randomUUID()
        )

        `when`(
            authService.register(
                username = "testuser",
                email = "test@example.com",
                password = "password123"
            )
        ).thenReturn(expectedUser)

        val result = authController.register(
            RegisterRequest(
                username = "testuser",
                email = "test@example.com",
                password = "password123"
            )
        )

        assertEquals(expectedUser, result)

        verify(authService).register(
            username = "testuser",
            email = "test@example.com",
            password = "password123"
        )
    }

    @Test
    fun `login returns authentication response`() {
        val expectedResponse = mapOf<String, Any>(
            "access_token" to "test-token"
        )

        `when`(
            authService.login(
                username = "testuser",
                password = "password123"
            )
        ).thenReturn(expectedResponse)

        val result = authController.login(
            LoginRequest(
                username = "testuser",
                password = "password123"
            )
        )

        assertEquals(expectedResponse, result)

        verify(authService).login(
            username = "testuser",
            password = "password123"
        )
    }
}
