package me.ultard.travo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "expense_split")
class ExpenseSplit(
    @Id
    @Column(updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "expense_id", nullable = false, updatable = false)
    val expenseId: UUID,

    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: UUID,

    @Column(name = "amount_cents", nullable = false)
    var amountCents: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", insertable = false, updatable = false)
    val expense: Expense? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null,
)
