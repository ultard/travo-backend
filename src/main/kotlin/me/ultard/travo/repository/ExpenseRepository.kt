package me.ultard.travo.repository

import me.ultard.travo.domain.Expense
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ExpenseRepository : JpaRepository<Expense, UUID> {

    fun findAllByTripIdOrderByCreatedAtDesc(tripId: UUID): List<Expense>

    @Query("SELECT e FROM Expense e JOIN FETCH e.paidBy WHERE e.tripId = :tripId ORDER BY e.createdAt DESC")
    fun findAllByTripIdWithPayerOrderByCreatedAtDesc(tripId: UUID): List<Expense>

    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.splits JOIN FETCH e.paidBy WHERE e.id = :id")
    fun findByIdWithSplits(id: UUID): Expense?
}
