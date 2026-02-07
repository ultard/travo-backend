package me.ultard.travo.service

import me.ultard.travo.dto.SettlementResponse
import me.ultard.travo.repository.ExpenseRepository
import me.ultard.travo.repository.ExpenseSplitRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SettlementService(
    private val expenseRepository: ExpenseRepository,
    private val expenseSplitRepository: ExpenseSplitRepository,
    private val tripService: TripService,
    private val userService: UserService,
) {

    fun getSettlements(tripId: UUID, currentUserId: UUID): List<SettlementResponse> {
        tripService.ensureParticipant(tripId, currentUserId)
        val trip = tripService.getById(tripId)
        val participants = tripService.getParticipants(tripId).map { it.userId }.toSet()
        val expenses = expenseRepository.findAllByTripIdOrderByCreatedAtDesc(tripId)

        val balance = mutableMapOf<UUID, Long>()
        participants.forEach { balance[it] = 0L }

        for (expense in expenses) {
            balance.merge(expense.paidById, expense.amountCents) { a, b -> a + b }
            expenseSplitRepository.findAllByExpenseId(expense.id).forEach { split ->
                balance.merge(split.userId, -split.amountCents) { a, b -> a + b }
            }
        }

        val debtRemaining = balance.filter { it.value < 0 }.mapValues { -it.value }.toMutableMap()
        val creditRemaining = balance.filter { it.value > 0 }.toMutableMap()

        val result = mutableListOf<SettlementResponse>()
        for ((debtorId, debtCents) in debtRemaining) {
            var remaining = debtCents
            for (creditorId in creditRemaining.keys.toList()) {
                val creditCents = creditRemaining[creditorId] ?: 0
                if (remaining <= 0 || creditCents <= 0) continue
                val transfer = minOf(remaining, creditCents)
                if (transfer <= 0) continue
                val fromUser = userService.getById(debtorId)
                val toUser = userService.getById(creditorId)
                result.add(
                    SettlementResponse(
                        fromUserId = debtorId,
                        fromUserDisplayName = fromUser.displayName,
                        toUserId = creditorId,
                        toUserDisplayName = toUser.displayName,
                        amountCents = transfer,
                        currencyCode = trip.currencyCode,
                    ),
                )
                remaining -= transfer
                creditRemaining[creditorId] = creditCents - transfer
                if (creditRemaining[creditorId] == 0L) creditRemaining.remove(creditorId)
            }
        }
        return result
    }
}
