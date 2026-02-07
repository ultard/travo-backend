package me.ultard.travo.dto

import java.util.UUID

/**
 * One user owes another user a sum (in trip currency).
 */
data class SettlementResponse(
    val fromUserId: UUID,
    val fromUserDisplayName: String,
    val toUserId: UUID,
    val toUserDisplayName: String,
    val amountCents: Long,
    val currencyCode: String,
)
