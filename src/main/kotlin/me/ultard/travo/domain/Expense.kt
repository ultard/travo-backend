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
    val id: UUID = UUID.randomUUID(),

    @Column(name = "trip_id", nullable = false, updatable = false)
    val tripId: UUID,

    @Column(name = "paid_by_id", nullable = false, updatable = false)
    val paidById: UUID,

    @Column(name = "amount_cents", nullable = false)
    var amountCents: Long,

    @Column(name = "currency_code", length = 3, nullable = false)
    var currencyCode: String,

    @Column(length = 500)
    var description: String? = null,

    @Column(length = 64)
    var category: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", insertable = false, updatable = false)
    val trip: Trip? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_id", insertable = false, updatable = false)
    val paidBy: User? = null,

    @OneToMany(mappedBy = "expense", cascade = [CascadeType.ALL], orphanRemoval = true)
    val splits: MutableList<ExpenseSplit> = mutableListOf(),
)
