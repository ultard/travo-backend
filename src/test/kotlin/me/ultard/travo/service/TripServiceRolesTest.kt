package me.ultard.travo.service

import me.ultard.travo.TestData
import me.ultard.travo.domain.ParticipantRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class TripServiceRolesTest(
    @Autowired private val userService: UserService,
    @Autowired private val tripService: TripService,
) {
    @Test
    fun `owner can promote member to admin`() {
        val ownerId = TestData.registerUser(userService, "o3@example.com", "O3")
        val memberId = TestData.registerUser(userService, "m3@example.com", "M3")
        val tripId = TestData.createTrip(tripService, ownerId, "T3")
        TestData.addParticipant(tripService, tripId, memberId, ownerId, ParticipantRole.MEMBER)

        val updated = tripService.updateParticipantRole(tripId, memberId, ParticipantRole.ADMIN, ownerId)
        assertEquals(ParticipantRole.ADMIN, updated.role)
    }

    @Test
    fun `cannot change own role`() {
        val ownerId = TestData.registerUser(userService, "o4@example.com", "O4")
        val tripId = TestData.createTrip(tripService, ownerId, "T4")
        assertThrows(IllegalArgumentException::class.java) {
            tripService.updateParticipantRole(tripId, ownerId, ParticipantRole.ADMIN, ownerId)
        }
    }

    @Test
    fun `owner cannot leave trip`() {
        val ownerId = TestData.registerUser(userService, "o5@example.com", "O5")
        val tripId = TestData.createTrip(tripService, ownerId, "T5")
        assertThrows(IllegalArgumentException::class.java) {
            tripService.leaveTrip(tripId, ownerId)
        }
    }
}

