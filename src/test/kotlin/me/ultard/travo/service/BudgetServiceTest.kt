package me.ultard.travo.service

import me.ultard.travo.TestData
import me.ultard.travo.dto.TripBudgetUpsertRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class BudgetServiceTest(
    @Autowired private val userService: UserService,
    @Autowired private val tripService: TripService,
    @Autowired private val budgetService: BudgetService,
) {
    @Test
    fun `upsert overwrites limit for same category`() {
        val userId = TestData.registerUser(userService, "b1@example.com", "B1")
        val tripId = TestData.createTrip(tripService, userId, "TB")

        val b1 = budgetService.upsert(tripId, TripBudgetUpsertRequest(category = "FOOD", limitCents = 1000), userId)
        val b2 = budgetService.upsert(tripId, TripBudgetUpsertRequest(category = "FOOD", limitCents = 2500), userId)

        assertEquals(b1.id, b2.id)
        assertEquals(2500, b2.limitCents)
        assertEquals(1, budgetService.list(tripId, userId).size)
    }
}

