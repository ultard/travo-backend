package me.ultard.travo.service

import me.ultard.travo.domain.TripBudget
import me.ultard.travo.dto.TripBudgetResponse
import me.ultard.travo.dto.TripBudgetUpsertRequest
import me.ultard.travo.repository.TripBudgetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class BudgetService(
    private val tripService: TripService,
    private val tripBudgetRepository: TripBudgetRepository,
) {
    @Transactional(readOnly = true)
    fun list(tripId: UUID, currentUserId: UUID): List<TripBudgetResponse> {
        tripService.ensureParticipant(tripId, currentUserId)
        return tripBudgetRepository.findAllByTripIdOrderByCreatedAtDesc(tripId).map(::toResponse)
    }

    @Transactional
    fun upsert(tripId: UUID, request: TripBudgetUpsertRequest, currentUserId: UUID): TripBudgetResponse {
        tripService.ensureParticipant(tripId, currentUserId)
        require(request.limitCents > 0) { "limitCents must be > 0" }
        val normalizedCategory = request.category?.trim()?.ifBlank { null }

        val existing = tripBudgetRepository.findByTripIdAndCategory(tripId, normalizedCategory)
        val saved = if (existing != null) {
            existing.limitCents = request.limitCents
            tripBudgetRepository.save(existing)
        } else {
            tripBudgetRepository.save(
                TripBudget(
                    tripId = tripId,
                    category = normalizedCategory,
                    limitCents = request.limitCents,
                ),
            )
        }
        return toResponse(saved)
    }

    @Transactional
    fun delete(tripId: UUID, budgetId: UUID, currentUserId: UUID) {
        tripService.ensureParticipant(tripId, currentUserId)
        val b = tripBudgetRepository.findById(budgetId).orElseThrow { NoSuchElementException("Budget not found: $budgetId") }
        require(b.tripId == tripId) { "Budget does not belong to trip" }
        tripBudgetRepository.delete(b)
    }

    private fun toResponse(b: TripBudget) = TripBudgetResponse(
        id = b.id,
        tripId = b.tripId,
        category = b.category,
        limitCents = b.limitCents,
        createdAt = b.createdAt,
    )
}

