
package com.lostnfound.api.user

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    fun createUser(
        username: String,
        email: String,
        displayName: String,
        authUserId: UUID
    ) {
        val sql = """
            INSERT INTO public.users
                (username, email, display_name, oauth_provider, auth_user_id)
            VALUES (?, ?, ?, 'email', ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            username,
            email,
            displayName,
            authUserId
        )
    }
    fun findByUsername(username: String): User? {
        val sql = """
            SELECT id, username, email, display_name, auth_user_id
            FROM public.users
            WHERE LOWER(username) = LOWER(?)
        """.trimIndent()

        val users = jdbcTemplate.query(sql, { rs, _ ->
            User(
                id = rs.getLong("id"),
                username = rs.getString("username"),
                email = rs.getString("email"),
                displayName = rs.getString("display_name"),
                authUserId = UUID.fromString(rs.getString("auth_user_id"))
            )
        }, username)
        return users.firstOrNull()


    }




}
