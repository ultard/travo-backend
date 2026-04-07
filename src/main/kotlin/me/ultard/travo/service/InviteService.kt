package me.ultard.travo.service

import me.ultard.travo.domain.ParticipantRole
import me.ultard.travo.domain.TripInvite
import me.ultard.travo.domain.TripInviteStatus
import me.ultard.travo.dto.TripInviteCreateRequest
import me.ultard.travo.dto.TripInviteResponse
import me.ultard.travo.repository.TripInviteRepository
import me.ultard.travo.repository.TripParticipantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import me.ultard.travo.domain.TripParticipant

@Service
class InviteService(
    private val userService: UserService,
    private val tripParticipantRepository: TripParticipantRepository,
    private val tripInviteRepository: TripInviteRepository,
) {
    @Transactional
    fun create(tripId: UUID, request: TripInviteCreateRequest, currentUserId: UUID, now: Instant = Instant.now()): TripInviteResponse {
        val current = tripParticipantRepository.findByTripIdAndUserId(tripId, currentUserId)
            ?: throw NoSuchElementException("Not a participant of trip $tripId")
        require(current.role == ParticipantRole.OWNER || current.role == ParticipantRole.ADMIN) {
            "Only owner or admin can create invites"
        }
        require(request.role != ParticipantRole.OWNER) { "Cannot invite as OWNER" }

        val email = request.email.trim()
        require(email.contains("@")) { "Invalid email" }

        val expiresAt = now.plus((request.expiresInHours ?: 72), ChronoUnit.HOURS)

        val existing = tripInviteRepository.findActivePendingByTripIdAndEmail(tripId, email, now)
        require(existing.isEmpty()) { "Active invite already exists for this email" }

        val invite = tripInviteRepository.save(
            TripInvite(
                tripId = tripId,
                email = email,
                role = request.role,
                createdById = currentUserId,
                expiresAt = expiresAt,
            ),
        )
        return toResponse(invite)
    }

    @Transactional(readOnly = true)
    fun listMyInvites(currentUserId: UUID, now: Instant = Instant.now()): List<TripInviteResponse> {
        val me = userService.getById(currentUserId)
        val invites = tripInviteRepository.findAllByEmailIgnoreCaseAndStatusOrderByCreatedAtDesc(me.email, TripInviteStatus.PENDING)
            .map { i ->
                if (i.expiresAt.isBefore(now)) {
                    i.status = TripInviteStatus.EXPIRED
                }
                i
            }
        return invites.filter { it.status == TripInviteStatus.PENDING && it.expiresAt.isAfter(now) }.map(::toResponse)
    }

    @Transactional
    fun accept(inviteId: UUID, currentUserId: UUID, now: Instant = Instant.now()) {
        val invite = tripInviteRepository.findById(inviteId).orElseThrow { NoSuchElementException("Invite not found: $inviteId") }
        val me = userService.getById(currentUserId)
        require(invite.email.equals(me.email, ignoreCase = true)) { "Invite is not for current user" }
        require(invite.status == TripInviteStatus.PENDING) { "Invite is not pending" }
        if (invite.expiresAt.isBefore(now)) {
            invite.status = TripInviteStatus.EXPIRED
            tripInviteRepository.save(invite)
            throw IllegalArgumentException("Invite expired")
        }
        invite.status = TripInviteStatus.ACCEPTED
        invite.respondedAt = now
        tripInviteRepository.save(invite)

        if (!tripParticipantRepository.existsByTripIdAndUserId(invite.tripId, currentUserId)) {
            tripParticipantRepository.save(
                TripParticipant(
                    tripId = invite.tripId,
                    userId = currentUserId,
                    role = invite.role,
                ),
            )
        }
    }

    @Transactional
    fun decline(inviteId: UUID, currentUserId: UUID, now: Instant = Instant.now()) {
        val invite = tripInviteRepository.findById(inviteId).orElseThrow { NoSuchElementException("Invite not found: $inviteId") }
        val me = userService.getById(currentUserId)
        require(invite.email.equals(me.email, ignoreCase = true)) { "Invite is not for current user" }
        require(invite.status == TripInviteStatus.PENDING) { "Invite is not pending" }
        invite.status = TripInviteStatus.DECLINED
        invite.respondedAt = now
        tripInviteRepository.save(invite)
    }

    private fun toResponse(i: TripInvite) = TripInviteResponse(
        id = i.id,
        tripId = i.tripId,
        email = i.email,
        role = i.role,
        status = i.status,
        createdById = i.createdById,
        createdAt = i.createdAt,
        expiresAt = i.expiresAt,
        respondedAt = i.respondedAt,
    )
}

