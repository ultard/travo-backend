package me.ultard.travo.dto

import me.ultard.travo.domain.ParticipantRole
import me.ultard.travo.domain.TripInviteStatus
import java.time.Instant
import java.util.UUID

data class TripInviteCreateRequest(
    val email: String,
    val role: ParticipantRole = ParticipantRole.MEMBER,
    val expiresInHours: Long? = null,
)

data class TripInviteResponse(
    val id: UUID,
    val tripId: UUID,
    val email: String,
    val role: ParticipantRole,
    val status: TripInviteStatus,
    val createdById: UUID,
    val createdAt: Instant,
    val expiresAt: Instant,
    val respondedAt: Instant?,
)

