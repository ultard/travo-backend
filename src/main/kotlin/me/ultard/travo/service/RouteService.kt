package me.ultard.travo.service

import me.ultard.travo.domain.TripRoute
import me.ultard.travo.dto.RoutePointCreateRequest
import me.ultard.travo.dto.RoutePointResponse
import me.ultard.travo.dto.RoutePointUpdateRequest
import me.ultard.travo.repository.TripRouteRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RouteService(
    private val tripRouteRepository: TripRouteRepository,
    private val tripService: TripService,
) {

    fun list(tripId: UUID, currentUserId: UUID): List<RoutePointResponse> {
        tripService.ensureParticipant(tripId, currentUserId)
        return tripRouteRepository.findAllByTripIdOrderByPosition(tripId).map(::toResponse)
    }

    fun create(tripId: UUID, request: RoutePointCreateRequest, currentUserId: UUID): RoutePointResponse {
        tripService.ensureParticipant(tripId, currentUserId)
        val route = TripRoute(
            tripId = tripId,
            position = request.position,
            name = request.name,
            description = request.description,
            address = request.address,
            latitude = request.latitude,
            longitude = request.longitude,
            plannedStartDate = request.plannedStartDate,
            plannedEndDate = request.plannedEndDate,
        )
        val saved = tripRouteRepository.save(route)
        return toResponse(saved)
    }

    fun update(tripId: UUID, routeId: UUID, request: RoutePointUpdateRequest, currentUserId: UUID): RoutePointResponse {
        tripService.ensureParticipant(tripId, currentUserId)
        val route = tripRouteRepository.findById(routeId).orElseThrow { NoSuchElementException("Route point not found: $routeId") }
        require(route.tripId == tripId) { "Route point does not belong to trip" }
        request.position?.let { route.position = it }
        request.name?.let { route.name = it }
        request.description?.let { route.description = it }
        request.address?.let { route.address = it }
        request.latitude?.let { route.latitude = it }
        request.longitude?.let { route.longitude = it }
        request.plannedStartDate?.let { route.plannedStartDate = it }
        request.plannedEndDate?.let { route.plannedEndDate = it }
        return toResponse(tripRouteRepository.save(route))
    }

    fun delete(tripId: UUID, routeId: UUID, currentUserId: UUID) {
        tripService.ensureParticipant(tripId, currentUserId)
        val route = tripRouteRepository.findById(routeId).orElseThrow { NoSuchElementException("Route point not found: $routeId") }
        require(route.tripId == tripId) { "Route point does not belong to trip" }
        tripRouteRepository.delete(route)
    }

    private fun toResponse(r: TripRoute) = RoutePointResponse(
        id = r.id,
        tripId = r.tripId,
        position = r.position,
        name = r.name,
        description = r.description,
        address = r.address,
        latitude = r.latitude,
        longitude = r.longitude,
        plannedStartDate = r.plannedStartDate,
        plannedEndDate = r.plannedEndDate,
    )
}
