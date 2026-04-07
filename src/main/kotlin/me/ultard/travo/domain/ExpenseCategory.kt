package me.ultard.travo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "expense_category")
class ExpenseCategory(
    @Id
    @Column(updatable = false, nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "code", nullable = false, length = 64, unique = true)
    var code: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),
)

