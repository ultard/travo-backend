package me.ultard.travo.service

import me.ultard.travo.TestData
import me.ultard.travo.domain.ParticipantRole
import me.ultard.travo.dto.TripInviteCreateRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class InviteServiceTest(
    @Autowired private val userService: UserService,
    @Autowired private val tripService: TripService,
    @Autowired private val inviteService: InviteService,
) {
    @Test
    fun `owner can create invite and user can accept`() {
        val ownerId = TestData.registerUser(userService, "owner@example.com", "Owner")
        val invitedId = TestData.registerUser(userService, "invited@example.com", "Invited")
        val tripId = TestData.createTrip(tripService, ownerId, "T1")

        val invite = inviteService.create(
            tripId = tripId,
            request = TripInviteCreateRequest(email = "invited@example.com", role = ParticipantRole.MEMBER),
            currentUserId = ownerId,
        )

        inviteService.accept(invite.id, invitedId)
        val participants = tripService.getParticipants(tripId)
        assertEquals(setOf(ownerId, invitedId), participants.map { it.userId }.toSet())
    }

    @Test
    fun `member cannot create invite`() {
        val ownerId = TestData.registerUser(userService, "owner2@example.com", "Owner2")
        val memberId = TestData.registerUser(userService, "m@example.com", "M")
        val tripId = TestData.createTrip(tripService, ownerId, "T2")
        TestData.addParticipant(tripService, tripId, memberId, ownerId, ParticipantRole.MEMBER)

        assertThrows(IllegalArgumentException::class.java) {
            inviteService.create(tripId, TripInviteCreateRequest(email = "x@y.com"), memberId)
        }
    }
}

