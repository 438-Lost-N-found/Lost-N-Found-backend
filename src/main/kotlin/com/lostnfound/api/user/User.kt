package com.lostnfound.api.user

import java.util.UUID

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val displayName: String,
    val authUserId: UUID
)
