package me.ultard.travo.repository

import me.ultard.travo.domain.ExpenseSplit
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ExpenseSplitRepository : JpaRepository<ExpenseSplit, UUID> {

    fun findAllByExpenseId(expenseId: UUID): List<ExpenseSplit>

    fun deleteAllByExpenseId(expenseId: UUID): Int
}
