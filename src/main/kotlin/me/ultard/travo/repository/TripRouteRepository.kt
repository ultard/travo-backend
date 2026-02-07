package me.ultard.travo.repository

import me.ultard.travo.domain.TripRoute
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TripRouteRepository : JpaRepository<TripRoute, UUID> {

    fun findAllByTripIdOrderByPosition(tripId: UUID): List<TripRoute>
}
