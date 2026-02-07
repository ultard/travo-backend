package me.ultard.travo.dto

import java.time.Instant
import java.util.UUID

data class ExpenseResponse(
    val id: UUID,
    val tripId: UUID,
    val paidById: UUID,
    val paidByDisplayName: String,
    val amountCents: Long,
    val currencyCode: String,
    val description: String?,
    val category: String?,
    val createdAt: Instant,
    val splits: List<ExpenseSplitResponse>,
)

data class ExpenseSplitResponse(
    val userId: UUID,
    val displayName: String,
    val amountCents: Long,
)

data class ExpenseCreateResponse(
    val id: UUID,
)

data class ExpenseCreateRequest(
    val paidById: UUID,
    val amountCents: Long,
    val currencyCode: String,
    val description: String? = null,
    val category: String? = null,
    val splits: List<ExpenseSplitRequest>,
)

data class ExpenseSplitRequest(
    val userId: UUID,
    val amountCents: Long,
)

data class ExpenseUpdateRequest(
    val amountCents: Long? = null,
    val currencyCode: String? = null,
    val description: String? = null,
    val category: String? = null,
    val splits: List<ExpenseSplitRequest>? = null,
)
