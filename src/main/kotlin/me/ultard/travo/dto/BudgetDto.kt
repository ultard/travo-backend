package me.ultard.travo.dto

import java.time.Instant
import java.util.UUID

data class TripBudgetUpsertRequest(
    val category: String? = null,
    val limitCents: Long,
)

data class TripBudgetResponse(
    val id: UUID,
    val tripId: UUID,
    val category: String?,
    val limitCents: Long,
    val createdAt: Instant,
)

