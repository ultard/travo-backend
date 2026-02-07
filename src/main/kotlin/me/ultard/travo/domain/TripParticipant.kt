package me.ultard.travo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

enum class ParticipantRole {
    OWNER,
    ADMIN,
    MEMBER,
}

@Entity
@Table(name = "trip_participant")
@IdClass(TripParticipantId::class)
class TripParticipant(
    @Id
    @Column(name = "trip_id", updatable = false, nullable = false)
    val tripId: UUID,

    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: ParticipantRole = ParticipantRole.MEMBER,

    @Column(name = "joined_at", nullable = false, updatable = false)
    val joinedAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", insertable = false, updatable = false)
    val trip: Trip? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null,
)

data class TripParticipantId(
    val tripId: UUID = UUID.randomUUID(),
    val userId: UUID = UUID.randomUUID(),
)
