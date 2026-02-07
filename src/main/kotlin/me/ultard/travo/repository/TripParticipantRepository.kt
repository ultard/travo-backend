package me.ultard.travo.repository

import me.ultard.travo.domain.TripParticipant
import me.ultard.travo.domain.TripParticipantId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface TripParticipantRepository : JpaRepository<TripParticipant, TripParticipantId> {

    fun findAllByTripId(tripId: UUID): List<TripParticipant>

    fun findByTripIdAndUserId(tripId: UUID, userId: UUID): TripParticipant?

    @Query("SELECT p FROM TripParticipant p JOIN FETCH p.user WHERE p.tripId = :tripId")
    fun findAllByTripIdWithUser(tripId: UUID): List<TripParticipant>

    fun existsByTripIdAndUserId(tripId: UUID, userId: UUID): Boolean

    @Modifying
    fun deleteByTripIdAndUserId(tripId: UUID, userId: UUID): Int
}
