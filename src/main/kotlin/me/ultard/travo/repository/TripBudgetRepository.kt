package me.ultard.travo.repository

import me.ultard.travo.domain.TripBudget
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TripBudgetRepository : JpaRepository<TripBudget, UUID> {
    fun findAllByTripIdOrderByCreatedAtDesc(tripId: UUID): List<TripBudget>
    fun findByTripIdAndCategory(tripId: UUID, category: String?): TripBudget?
}

