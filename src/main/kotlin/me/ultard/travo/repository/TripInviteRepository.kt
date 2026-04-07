package me.ultard.travo.repository

import me.ultard.travo.domain.TripInvite
import me.ultard.travo.domain.TripInviteStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface TripInviteRepository : JpaRepository<TripInvite, UUID> {
    fun findAllByEmailIgnoreCaseAndStatusOrderByCreatedAtDesc(email: String, status: TripInviteStatus): List<TripInvite>

    @Query(
        """
        SELECT i FROM TripInvite i
        WHERE i.tripId = :tripId AND lower(i.email) = lower(:email) AND i.status = me.ultard.travo.domain.TripInviteStatus.PENDING
        AND i.expiresAt > :now
        """,
    )
    fun findActivePendingByTripIdAndEmail(tripId: UUID, email: String, now: Instant): List<TripInvite>
}

