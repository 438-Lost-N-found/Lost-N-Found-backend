package com.lostnfound.api.user

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.jdbc.core.JdbcTemplate
import java.util.UUID

class UserRepositoryTest {

    private val jdbcTemplate = mock(JdbcTemplate::class.java)

    private val userRepository = UserRepository(jdbcTemplate)

    @Test
    fun `createUser inserts a new user`() {
        val authUserId = UUID.randomUUID()

        userRepository.createUser(
            username = "testuser",
            email = "test@example.com",
            displayName = "Test User",
            authUserId = authUserId
        )

        verify(jdbcTemplate).update(
            """
            INSERT INTO public.users
                (username, email, display_name, oauth_provider, auth_user_id)
            VALUES (?, ?, ?, 'email', ?)
            """.trimIndent(),
            "testuser",
            "test@example.com",
            "Test User",
            authUserId
        )
    }
}
