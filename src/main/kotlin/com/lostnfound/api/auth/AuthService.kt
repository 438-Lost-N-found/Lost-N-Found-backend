
package com.lostnfound.api.auth

import com.lostnfound.api.user.User
import com.lostnfound.api.user.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthService(
    private val supabaseAuthClient: SupabaseAuthClient,
    private val userRepository: UserRepository) {
    fun register(
        username: String,
        email: String,
        password: String

    ): User {

        val cleanUsername = username.trim()
        val cleanEmail = email.trim()

        if (userRepository.findByUsername(cleanUsername) != null) {
            throw IllegalArgumentException("Username is already taken.")
        }
        val response = supabaseAuthClient.register(
            cleanUsername,
            cleanEmail,
            password

        )

        val authUser = response["user"] as? Map<*, *> ?: throw IllegalStateException("Supabase did not return user information")
        val authUserId = authUser["id"] as? String ?: throw IllegalStateException("Supabase did not return a user ID")

        userRepository.createUser(
            username = cleanUsername,
            email = cleanEmail,
            displayName = cleanUsername,
            authUserId = UUID.fromString(authUserId)
        )

        return userRepository.findByUsername(cleanUsername)
            ?: throw IllegalStateException("User was not found after registration.")



    }

    fun login(username: String, password: String): Map<String, Any> {
        val cleanUsername = username.trim()
        val user = userRepository.findByUsername(cleanUsername)

            ?: throw IllegalArgumentException("Invalid username or password")

        return supabaseAuthClient.login(
            user.email,
            password
        )
    }
}
