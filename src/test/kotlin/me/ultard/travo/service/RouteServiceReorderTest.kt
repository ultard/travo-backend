package me.ultard.travo.service

import me.ultard.travo.TestData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class RouteServiceReorderTest(
    @Autowired private val userService: UserService,
    @Autowired private val tripService: TripService,
    @Autowired private val routeService: RouteService,
) {
    @Test
    fun `reorder assigns positions 1 to N`() {
        val userId = TestData.registerUser(userService, "r1@example.com", "R1")
        val tripId = TestData.createTrip(tripService, userId, "TR")

        val a = TestData.addRoutePoint(routeService, tripId, userId, 1, "A")
        val b = TestData.addRoutePoint(routeService, tripId, userId, 2, "B")
        val c = TestData.addRoutePoint(routeService, tripId, userId, 3, "C")

        routeService.reorder(tripId, listOf(c, a, b), userId)
        val list = routeService.list(tripId, userId)

        assertEquals(listOf(c, a, b), list.map { it.id })
        assertEquals(listOf(1, 2, 3), list.map { it.position })
    }
}

