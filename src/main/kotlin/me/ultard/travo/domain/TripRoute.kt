package me.ultard.travo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "trip_route")
class TripRoute(
    @Id
    @Column(updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "trip_id", nullable = false, updatable = false)
    val tripId: UUID,

    @Column(nullable = false)
    var position: Int,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "text")
    var description: String? = null,

    @Column(length = 500)
    var address: String? = null,

    @Column(precision = 10, scale = 7)
    var latitude: BigDecimal? = null,

    @Column(precision = 10, scale = 7)
    var longitude: BigDecimal? = null,

    @Column(name = "planned_start_date")
    var plannedStartDate: LocalDate? = null,

    @Column(name = "planned_end_date")
    var plannedEndDate: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", insertable = false, updatable = false)
    val trip: Trip? = null,
)
