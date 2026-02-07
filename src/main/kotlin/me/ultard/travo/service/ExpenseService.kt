package me.ultard.travo.service

import me.ultard.travo.dto.ExpenseCreateRequest
import me.ultard.travo.dto.ExpenseCreateResponse
import me.ultard.travo.dto.ExpenseResponse
import me.ultard.travo.dto.ExpenseSplitResponse
import me.ultard.travo.dto.ExpenseUpdateRequest
import me.ultard.travo.domain.Expense
import me.ultard.travo.domain.ExpenseSplit
import me.ultard.travo.repository.ExpenseRepository
import me.ultard.travo.repository.ExpenseSplitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ExpenseService(
    private val expenseRepository: ExpenseRepository,
    private val expenseSplitRepository: ExpenseSplitRepository,
    private val tripService: TripService,
    private val userService: UserService,
) {

    fun create(tripId: UUID, request: ExpenseCreateRequest, currentUserId: UUID): ExpenseCreateResponse {
        tripService.ensureParticipant(tripId, currentUserId)
        require(request.splits.sumOf { it.amountCents } == request.amountCents) {
            "Sum of splits must equal expense amount"
        }
        val expense = Expense(
            tripId = tripId,
            paidById = request.paidById,
            amountCents = request.amountCents,
            currencyCode = request.currencyCode,
            description = request.description,
            category = request.category,
        )
        val saved = expenseRepository.save(expense)
        request.splits.forEach { s ->
            expenseSplitRepository.save(
                ExpenseSplit(
                    expenseId = saved.id,
                    userId = s.userId,
                    amountCents = s.amountCents,
                ),
            )
        }

        return ExpenseCreateResponse(id = saved.id)
    }

    @Transactional(readOnly = true)
    fun listByTrip(tripId: UUID, currentUserId: UUID): List<ExpenseResponse> {
        tripService.ensureParticipant(tripId, currentUserId)
        val expenses = expenseRepository.findAllByTripIdWithPayerOrderByCreatedAtDesc(tripId)
        return expenses.map { toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getById(tripId: UUID, expenseId: UUID, currentUserId: UUID): ExpenseResponse {
        tripService.ensureParticipant(tripId, currentUserId)
        val expense = expenseRepository.findByIdWithSplits(expenseId) ?: throw NoSuchElementException("Expense not found: $expenseId")
        require(expense.tripId == tripId) { "Expense does not belong to trip" }
        return toResponse(expense)
    }

    @Transactional
    fun update(tripId: UUID, expenseId: UUID, request: ExpenseUpdateRequest, currentUserId: UUID): ExpenseResponse {
        tripService.ensureParticipant(tripId, currentUserId)
        val expense = expenseRepository.findById(expenseId).orElseThrow { NoSuchElementException("Expense not found: $expenseId") }
        require(expense.tripId == tripId) { "Expense does not belong to trip" }
        request.amountCents?.let { expense.amountCents = it }
        request.currencyCode?.let { expense.currencyCode = it }
        request.description?.let { expense.description = it }
        request.category?.let { expense.category = it }
        request.splits?.let { splits ->
            require(splits.sumOf { it.amountCents } == expense.amountCents) {
                "Sum of splits must equal expense amount"
            }
            expenseSplitRepository.deleteAllByExpenseId(expenseId)
            splits.forEach { s ->
                expenseSplitRepository.save(
                    ExpenseSplit(
                        expenseId = expenseId,
                        userId = s.userId,
                        amountCents = s.amountCents,
                    ),
                )
            }
        }
        expenseRepository.save(expense)
        return toResponse(expenseRepository.findByIdWithSplits(expenseId)!!)
    }

    fun delete(tripId: UUID, expenseId: UUID, currentUserId: UUID) {
        tripService.ensureParticipant(tripId, currentUserId)
        val expense = expenseRepository.findById(expenseId).orElseThrow { NoSuchElementException("Expense not found: $expenseId") }
        require(expense.tripId == tripId) { "Expense does not belong to trip" }
        expenseRepository.delete(expense)
    }

    private fun toResponse(expense: Expense): ExpenseResponse {
        val splits = expense.splits.map { s ->
            val u = userService.getById(s.userId)
            ExpenseSplitResponse(userId = s.userId, displayName = u.displayName, amountCents = s.amountCents)
        }
        return ExpenseResponse(
            id = expense.id,
            tripId = expense.tripId,
            paidById = expense.paidById,
            paidByDisplayName = expense.paidBy?.displayName ?: "",
            amountCents = expense.amountCents,
            currencyCode = expense.currencyCode,
            description = expense.description,
            category = expense.category,
            createdAt = expense.createdAt,
            splits = splits,
        )
    }
}
