package me.ultard.travo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "trip")
class Trip(
    @Id
    @Column(updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "text")
    var description: String? = null,

    @Column(name = "start_date")
    var startDate: LocalDate? = null,

    @Column(name = "end_date")
    var endDate: LocalDate? = null,

    @Column(name = "currency_code", length = 3)
    var currencyCode: String = "RUB",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false, updatable = false)
    val createdBy: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "trip", orphanRemoval = true)
    val participants: MutableList<TripParticipant> = mutableListOf(),

    @OneToMany(mappedBy = "trip", orphanRemoval = true)
    val expenses: MutableList<Expense> = mutableListOf(),

    @OneToMany(mappedBy = "trip", orphanRemoval = true)
    val routePoints: MutableList<TripRoute> = mutableListOf(),
)
