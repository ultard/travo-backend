package me.ultard.travo.service

import me.ultard.travo.TestData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class RefreshTokenServiceTest(
    @Autowired private val userService: UserService,
    @Autowired private val refreshTokenService: RefreshTokenService,
) {
    @Test
    fun `rotate revokes old token and returns userId`() {
        val userId = TestData.registerUser(userService, "a@example.com", "A")
        val t1 = refreshTokenService.issueForUser(userId)
        val rotatedUserId = refreshTokenService.rotate(t1)
        val t2 = refreshTokenService.issueForUser(userId)

        assertEquals(userId, rotatedUserId)
        assertNotEquals(t1, t2)
        assertTrue(t2.isNotBlank())
        assertThrows(IllegalArgumentException::class.java) {
            refreshTokenService.rotate(t1)
        }
    }
}

