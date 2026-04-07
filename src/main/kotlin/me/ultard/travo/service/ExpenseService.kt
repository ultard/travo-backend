package me.ultard.travo.service

import me.ultard.travo.dto.ExpenseCreateRequest
import me.ultard.travo.dto.ExpenseCreateResponse
import me.ultard.travo.dto.ExpensePageResponse
import me.ultard.travo.dto.ExpenseResponse
import me.ultard.travo.dto.ExpenseSplitResponse
import me.ultard.travo.dto.ExpenseUpdateRequest
import me.ultard.travo.domain.Expense
import me.ultard.travo.domain.ExpenseSplit
import me.ultard.travo.repository.ExpenseCategoryRepository
import me.ultard.travo.repository.ExpenseRepository
import me.ultard.travo.repository.ExpenseSplitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.PageRequest
import java.time.Instant
import java.util.Base64
import java.util.UUID

@Service
class ExpenseService(
    private val expenseRepository: ExpenseRepository,
    private val expenseSplitRepository: ExpenseSplitRepository,
    private val expenseCategoryRepository: ExpenseCategoryRepository,
    private val tripService: TripService,
    private val userService: UserService,
) {

    fun create(tripId: UUID, request: ExpenseCreateRequest, currentUserId: UUID): ExpenseCreateResponse {
        tripService.ensureParticipant(tripId, currentUserId)
        require(request.splits.sumOf { it.amountCents } == request.amountCents) {
            "Sum of splits must equal expense amount"
        }
        request.categoryId?.let { categoryId ->
            require(expenseCategoryRepository.existsById(categoryId)) { "Expense category not found: $categoryId" }
        }
        val expense = Expense(
            tripId = tripId,
            paidById = request.paidById,
            amountCents = request.amountCents,
            currencyCode = request.currencyCode,
            description = request.description,
            categoryId = request.categoryId,
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
    fun listByTripPaged(
        tripId: UUID,
        currentUserId: UUID,
        from: Instant?,
        to: Instant?,
        categoryId: UUID?,
        payer: UUID?,
        limit: Int,
        cursor: String?,
    ): ExpensePageResponse {
        tripService.ensureParticipant(tripId, currentUserId)
        val pageSize = limit.coerceIn(1, 100) + 1
        val (cursorCreatedAt, cursorId) = decodeCursor(cursor)

        val items = expenseRepository.findByTripPaged(
            tripId = tripId,
            from = from,
            to = to,
            categoryId = categoryId,
            payer = payer,
            cursorCreatedAt = cursorCreatedAt,
            cursorId = cursorId,
            pageable = PageRequest.of(0, pageSize),
        )

        val effectiveLimit = limit.coerceIn(1, 100)
        val hasNext = items.size > effectiveLimit
        val page = if (hasNext) items.take(effectiveLimit) else items
        val responses = page.map { toResponse(it) }
        val nextCursor = if (hasNext) encodeCursor(page.last().createdAt, page.last().id) else null
        return ExpensePageResponse(items = responses, nextCursor = nextCursor)
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
        request.categoryId?.let { categoryId ->
            require(expenseCategoryRepository.existsById(categoryId)) { "Expense category not found: $categoryId" }
            expense.categoryId = categoryId
        }
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

    @Transactional(readOnly = true)
    fun listFeed(
        currentUserId: UUID,
        from: Instant?,
        to: Instant?,
        categoryId: UUID?,
        payer: UUID?,
        limit: Int,
        cursor: String?,
    ): ExpensePageResponse {
        val pageSize = limit.coerceIn(1, 100) + 1
        val (cursorCreatedAt, cursorId) = decodeCursor(cursor)

        val items = expenseRepository.findFeedForUser(
            userId = currentUserId,
            from = from,
            to = to,
            categoryId = categoryId,
            payer = payer,
            cursorCreatedAt = cursorCreatedAt,
            cursorId = cursorId,
            pageable = PageRequest.of(0, pageSize),
        )

        val effectiveLimit = limit.coerceIn(1, 100)
        val hasNext = items.size > effectiveLimit
        val page = if (hasNext) items.take(effectiveLimit) else items
        val responses = page.map { toResponse(it) }
        val nextCursor = if (hasNext) encodeCursor(page.last().createdAt, page.last().id) else null
        return ExpensePageResponse(items = responses, nextCursor = nextCursor)
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
            categoryId = expense.categoryId,
            categoryCode = expense.category?.code,
            createdAt = expense.createdAt,
            splits = splits,
        )
    }

    private fun encodeCursor(createdAt: Instant, id: UUID): String {
        val raw = "${createdAt.toEpochMilli()}|$id"
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.toByteArray())
    }

    private fun decodeCursor(cursor: String?): Pair<Instant?, UUID?> {
        if (cursor.isNullOrBlank()) return null to null
        return try {
            val decoded = String(Base64.getUrlDecoder().decode(cursor))
            val parts = decoded.split("|", limit = 2)
            if (parts.size != 2) return null to null
            val createdAt = Instant.ofEpochMilli(parts[0].toLong())
            val id = UUID.fromString(parts[1])
            createdAt to id
        } catch (_: Exception) {
            null to null
        }
    }
}
