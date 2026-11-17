
package com.lostnfound.api.auth

import com.lostnfound.api.user.User
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String


)
data class LoginRequest(
    val username: String,
    val password: String

)

@RestController
@RequestMapping("/api/auth")

class AuthController(private val authService: AuthService) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: RegisterRequest): User {
        return authService.register(username = request.username, email = request.email, password = request.password)


    }
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): Map<String, Any> {
        return authService.login(username = request.username, password = request.password)
    }
}
