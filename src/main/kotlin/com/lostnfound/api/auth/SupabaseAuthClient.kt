
package com.lostnfound.api.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SupabaseAuthClient(
    @Value("\${supabase.url}") supabaseUrl: String, @Value("\${supabase.anon-key}") anonKey: String) {
    private val restClient = RestClient.builder()
        .baseUrl(supabaseUrl)
        .defaultHeader("apikey", anonKey)
        .defaultHeader("Authorization", "Bearer $anonKey")
        .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .build()


    fun register(
        username: String,
        email: String,
        password: String
    ): Map<String, Any> {
        return restClient.post()
            .uri("/auth/v1/signup")
            .body(
                mapOf(
                    "email" to email,
                    "password" to password,
                    "data" to mapOf("username" to username)
                )
            )
            .retrieve()
            .body(object : ParameterizedTypeReference<Map<String, Any>>() {})
            ?: emptyMap()
    }

    fun login(email: String, password: String): Map<String, Any> {
        return restClient.post()
            .uri("/auth/v1/token?grant_type=password")
            .body(
                mapOf(
                    "email" to email,
                    "password" to password
                )
            )
            .retrieve()
            .body(object : ParameterizedTypeReference<Map<String, Any>>() {})
            ?: emptyMap()
    }
}
