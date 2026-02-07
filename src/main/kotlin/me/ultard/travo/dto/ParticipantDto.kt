package me.ultard.travo.dto

import me.ultard.travo.domain.ParticipantRole
import java.time.Instant
import java.util.UUID

data class ParticipantResponse(
    val tripId: UUID,
    val userId: UUID,
    val displayName: String,
    val email: String,
    val role: ParticipantRole,
    val joinedAt: Instant,
)

data class ParticipantAddRequest(
    val userId: UUID,
    val role: ParticipantRole = ParticipantRole.MEMBER,
)
