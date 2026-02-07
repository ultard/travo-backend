package me.ultard.travo.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class RoutePointResponse(
    val id: UUID,
    val tripId: UUID,
    val position: Int,
    val name: String,
    val description: String?,
    val address: String?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val plannedStartDate: LocalDate?,
    val plannedEndDate: LocalDate?,
)

data class RoutePointCreateRequest(
    val position: Int,
    val name: String,
    val description: String? = null,
    val address: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    val plannedStartDate: LocalDate? = null,
    val plannedEndDate: LocalDate? = null,
)

data class RoutePointUpdateRequest(
    val position: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    val plannedStartDate: LocalDate? = null,
    val plannedEndDate: LocalDate? = null,
)
