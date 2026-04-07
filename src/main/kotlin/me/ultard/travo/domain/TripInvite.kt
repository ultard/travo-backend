package me.ultard.travo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

enum class TripInviteStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    EXPIRED,
}

@Entity
@Table(name = "trip_invite")
class TripInvite(
    @Id
    @Column(updatable = false, nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "trip_id", nullable = false, updatable = false)
    var tripId: UUID,

    @Column(name = "email", nullable = false)
    var email: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: ParticipantRole = ParticipantRole.MEMBER,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TripInviteStatus = TripInviteStatus.PENDING,

    @Column(name = "created_by_id", nullable = false, updatable = false)
    var createdById: UUID,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "expires_at", nullable = false, updatable = false)
    var expiresAt: Instant,

    @Column(name = "responded_at")
    var respondedAt: Instant? = null,
)

