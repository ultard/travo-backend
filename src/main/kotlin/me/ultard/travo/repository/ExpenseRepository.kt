package me.ultard.travo.repository

import me.ultard.travo.domain.Expense
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.util.UUID

interface ExpenseRepository : JpaRepository<Expense, UUID> {

    fun findAllByTripIdOrderByCreatedAtDesc(tripId: UUID): List<Expense>

    @Query("SELECT e FROM Expense e JOIN FETCH e.paidBy WHERE e.tripId = :tripId ORDER BY e.createdAt DESC")
    fun findAllByTripIdWithPayerOrderByCreatedAtDesc(tripId: UUID): List<Expense>

    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.splits JOIN FETCH e.paidBy WHERE e.id = :id")
    fun findByIdWithSplits(id: UUID): Expense?

    @Query(
        """
        SELECT e FROM Expense e
        JOIN FETCH e.paidBy
        WHERE e.tripId = :tripId
          AND (:from IS NULL OR e.createdAt >= :from)
          AND (:to IS NULL OR e.createdAt <= :to)
          AND (:categoryId IS NULL OR e.categoryId = :categoryId)
          AND (:payer IS NULL OR e.paidById = :payer)
          AND (
            :cursorCreatedAt IS NULL
            OR e.createdAt < :cursorCreatedAt
            OR (e.createdAt = :cursorCreatedAt AND e.id < :cursorId)
          )
        ORDER BY e.createdAt DESC, e.id DESC
        """,
    )
    fun findByTripPaged(
        tripId: UUID,
        from: Instant?,
        to: Instant?,
        categoryId: UUID?,
        payer: UUID?,
        cursorCreatedAt: Instant?,
        cursorId: UUID?,
        pageable: Pageable,
    ): List<Expense>

    @Query(
        """
        SELECT e FROM Expense e
        JOIN FETCH e.paidBy
        WHERE e.tripId IN (SELECT p.tripId FROM TripParticipant p WHERE p.userId = :userId)
          AND (:from IS NULL OR e.createdAt >= :from)
          AND (:to IS NULL OR e.createdAt <= :to)
          AND (:categoryId IS NULL OR e.categoryId = :categoryId)
          AND (:payer IS NULL OR e.paidById = :payer)
          AND (
            :cursorCreatedAt IS NULL
            OR e.createdAt < :cursorCreatedAt
            OR (e.createdAt = :cursorCreatedAt AND e.id < :cursorId)
          )
        ORDER BY e.createdAt DESC, e.id DESC
        """,
    )
    fun findFeedForUser(
        userId: UUID,
        from: Instant?,
        to: Instant?,
        categoryId: UUID?,
        payer: UUID?,
        cursorCreatedAt: Instant?,
        cursorId: UUID?,
        pageable: Pageable,
    ): List<Expense>
}
