package me.ultard.travo.dto

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val displayName: String,
)

data class UserRegisterRequest(
    val email: String,
    val displayName: String,
    val password: String,
)

data class UserLoginRequest(
    val email: String,
    val password: String,
)

data class JwtLoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse,
)

data class JwtRefreshRequest(
    val refreshToken: String,
)
