package me.ultard.travo.dto

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class TripResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val currencyCode: String,
    val createdById: UUID,
    val createdAt: Instant,
)

data class TripCreateResponse(
    val id: UUID,
)

data class TripCreateRequest(
    val name: String,
    val description: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val currencyCode: String = "RUB",
)

data class TripUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val currencyCode: String? = null,
)
