package me.ultard.travo.service

import me.ultard.travo.dto.ParticipantAddRequest
import me.ultard.travo.dto.ParticipantResponse
import me.ultard.travo.dto.TripCreateRequest
import me.ultard.travo.dto.TripCreateResponse
import me.ultard.travo.dto.TripResponse
import me.ultard.travo.dto.TripUpdateRequest
import me.ultard.travo.domain.ParticipantRole
import me.ultard.travo.domain.Trip
import me.ultard.travo.domain.TripParticipant
import me.ultard.travo.repository.TripParticipantRepository
import me.ultard.travo.repository.TripRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TripService(
    private val tripRepository: TripRepository,
    private val tripParticipantRepository: TripParticipantRepository,
    private val userService: UserService,
) {

    fun create(creatorId: UUID, request: TripCreateRequest): TripCreateResponse {
        val creator = userService.getById(creatorId)
        val trip = Trip(
            name = request.name,
            description = request.description,
            startDate = request.startDate,
            endDate = request.endDate,
            currencyCode = request.currencyCode,
            createdBy = creator,
        )
        val saved = tripRepository.save(trip)
        tripParticipantRepository.save(
            TripParticipant(
                tripId = saved.id,
                userId = creatorId,
                role = ParticipantRole.OWNER,
            ),
        )
        return TripCreateResponse(id = saved.id)
    }

    fun getById(id: UUID): Trip = tripRepository.findById(id).orElseThrow { NoSuchElementException("Trip not found: $id") }

    fun get(id: UUID, currentUserId: UUID): TripResponse {
        val trip = getById(id)
        ensureParticipant(id, currentUserId)
        return toResponse(trip)
    }

    fun listByUser(userId: UUID): List<TripResponse> {
        val trips = tripRepository.findAllByParticipantUserId(userId)
        return trips.map { toResponse(it) }
    }

    fun update(tripId: UUID, request: TripUpdateRequest, currentUserId: UUID): TripResponse {
        val trip = getById(tripId)
        ensureParticipant(tripId, currentUserId)
        request.name?.let { trip.name = it }
        request.description?.let { trip.description = it }
        request.startDate?.let { trip.startDate = it }
        request.endDate?.let { trip.endDate = it }
        request.currencyCode?.let { trip.currencyCode = it }
        val saved = tripRepository.save(trip)
        return toResponse(saved)
    }

    fun delete(tripId: UUID, currentUserId: UUID) {
        ensureOwner(tripId, currentUserId)
        tripRepository.deleteById(tripId)
    }

    fun getParticipants(tripId: UUID): List<ParticipantResponse> {
        val participants = tripParticipantRepository.findAllByTripIdWithUser(tripId)
        return participants.map { participant ->
            val user = participant.user!!
            ParticipantResponse(
                tripId = participant.tripId,
                userId = participant.userId,
                displayName = user.displayName,
                email = user.email,
                role = participant.role,
                joinedAt = participant.joinedAt,
            )
        }
    }

    fun addParticipant(tripId: UUID, request: ParticipantAddRequest, currentUserId: UUID): ParticipantResponse {
        ensureParticipant(tripId, currentUserId)

        require(request.role != ParticipantRole.OWNER) {
            "Cannot add participant as owner"
        }

        require(!tripParticipantRepository.existsByTripIdAndUserId(tripId, request.userId)) {
            "User already in trip"
        }

        val user = userService.getById(request.userId)
        val participant = tripParticipantRepository.save(
            TripParticipant(
                tripId = tripId,
                userId = request.userId,
                role = request.role,
            ),
        )

        return ParticipantResponse(
            tripId = participant.tripId,
            userId = participant.userId,
            displayName = user.displayName,
            email = user.email,
            role = participant.role,
            joinedAt = participant.joinedAt,
        )
    }

    fun removeParticipant(tripId: UUID, userId: UUID, currentUserId: UUID) {
        ensureCanRemoveParticipant(tripId, currentUserId, userId)
        tripParticipantRepository.deleteByTripIdAndUserId(tripId, userId)
    }

    private fun ensureCanRemoveParticipant(tripId: UUID, currentUserId: UUID, targetUserId: UUID) {
        if (currentUserId == targetUserId) {
            require(false) { "Cannot remove yourself; leave trip is not supported" }
        }
        val current = tripParticipantRepository.findByTripIdAndUserId(tripId, currentUserId)
            ?: throw NoSuchElementException("Not a participant of trip $tripId")
        val target = tripParticipantRepository.findByTripIdAndUserId(tripId, targetUserId)
            ?: throw NoSuchElementException("Participant not found in trip")
        when (current.role) {
            ParticipantRole.OWNER -> { /* может исключить любого */ }
            ParticipantRole.ADMIN -> require(target.role == ParticipantRole.MEMBER) {
                "Admin can only remove members, not other admins or the owner"
            }
            ParticipantRole.MEMBER -> require(false) {
                "Only owner or admin can remove participants"
            }
        }
    }

    fun ensureParticipant(tripId: UUID, userId: UUID) {
        require(tripParticipantRepository.existsByTripIdAndUserId(tripId, userId)) {
            "Not a participant of trip $tripId"
        }
    }

    fun ensureOwner(tripId: UUID, userId: UUID) {
        val p = tripParticipantRepository.findByTripIdAndUserId(tripId, userId)
            ?: throw NoSuchElementException("Not a participant of trip $tripId")
        require(p.role == ParticipantRole.OWNER) { "Only trip owner can perform this action" }
    }

    private fun toResponse(trip: Trip): TripResponse = TripResponse(
        id = trip.id,
        name = trip.name,
        description = trip.description,
        startDate = trip.startDate,
        endDate = trip.endDate,
        currencyCode = trip.currencyCode,
        createdById = trip.createdBy.id,
        createdAt = trip.createdAt,
    )
}
