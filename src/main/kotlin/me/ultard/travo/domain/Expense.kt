package me.ultard.travo.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "expense")
class Expense(
    @Id
    @Column(updatable = false, nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "trip_id", nullable = false, updatable = false)
    var tripId: UUID,

    @Column(name = "paid_by_id", nullable = false, updatable = false)
    var paidById: UUID,

    @Column(name = "amount_cents", nullable = false)
    var amountCents: Long,

    @Column(name = "currency_code", length = 3, nullable = false)
    var currencyCode: String,

    @Column(length = 500)
    var description: String? = null,

    @Column(name = "category_id")
    var categoryId: UUID,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", insertable = false, updatable = false)
    var trip: Trip? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_id", insertable = false, updatable = false)
    var paidBy: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    var category: ExpenseCategory? = null,

    @OneToMany(mappedBy = "expense", cascade = [CascadeType.ALL], orphanRemoval = true)
    var splits: MutableList<ExpenseSplit> = mutableListOf(),
)
