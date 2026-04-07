package me.ultard.travo.service

import me.ultard.travo.dto.ExpenseCategoryCreateRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class ExpenseCategoryServiceTest(
    @Autowired private val expenseCategoryService: ExpenseCategoryService,
) {
    @Test
    fun `create normalizes code to uppercase`() {
        val created = expenseCategoryService.create(ExpenseCategoryCreateRequest(code = "food"))
        assertEquals("FOOD", created.code)
    }

    @Test
    fun `cannot create duplicate code ignoring case`() {
        expenseCategoryService.create(ExpenseCategoryCreateRequest(code = "TAXI"))
        assertThrows(IllegalArgumentException::class.java) {
            expenseCategoryService.create(ExpenseCategoryCreateRequest(code = "taxi"))
        }
    }
}

