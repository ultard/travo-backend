package me.ultard.travo.repository

import me.ultard.travo.domain.Trip
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface TripRepository : JpaRepository<Trip, UUID> {

    @Query("SELECT t FROM Trip t JOIN t.participants p WHERE p.userId = :userId ORDER BY t.createdAt DESC")
    fun findAllByParticipantUserId(userId: UUID): List<Trip>
}
