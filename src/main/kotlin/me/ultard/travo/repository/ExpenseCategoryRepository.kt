package me.ultard.travo.repository

import me.ultard.travo.domain.ExpenseCategory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ExpenseCategoryRepository : JpaRepository<ExpenseCategory, UUID> {
    fun findAllByOrderByCodeAsc(): List<ExpenseCategory>
    fun existsByCodeIgnoreCase(code: String): Boolean
}

