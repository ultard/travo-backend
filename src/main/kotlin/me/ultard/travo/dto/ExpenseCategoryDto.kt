package me.ultard.travo.dto

import java.time.Instant
import java.util.UUID

data class ExpenseCategoryCreateRequest(
    val code: String,
)

data class ExpenseCategoryResponse(
    val id: UUID,
    val code: String,
    val createdAt: Instant,
)

