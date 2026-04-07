package me.ultard.travo

import me.ultard.travo.domain.ParticipantRole
import me.ultard.travo.dto.ParticipantAddRequest
import me.ultard.travo.dto.RoutePointCreateRequest
import me.ultard.travo.dto.TripCreateRequest
import me.ultard.travo.dto.UserRegisterRequest
import me.ultard.travo.service.RouteService
import me.ultard.travo.service.TripService
import me.ultard.travo.service.UserService
import java.time.LocalDate
import java.util.UUID

object TestData {
    fun registerUser(userService: UserService, email: String, displayName: String, password: String = "pass"): UUID {
        val u = userService.register(UserRegisterRequest(email = email, displayName = displayName, password = password))
        return u.id
    }

    fun createTrip(tripService: TripService, creatorId: UUID, name: String = "Trip"): UUID {
        val req = TripCreateRequest(
            name = name,
            description = null,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(1),
            currencyCode = "RUB",
        )
        return tripService.create(creatorId, req).id
    }

    fun addParticipant(tripService: TripService, tripId: UUID, addedUserId: UUID, byUserId: UUID, role: ParticipantRole): UUID {
        tripService.addParticipant(tripId, ParticipantAddRequest(userId = addedUserId, role = role), byUserId)
        return addedUserId
    }

    fun addRoutePoint(routeService: RouteService, tripId: UUID, userId: UUID, position: Int, name: String): UUID {
        return routeService.create(
            tripId,
            RoutePointCreateRequest(position = position, name = name),
            userId,
        ).id
    }
}

