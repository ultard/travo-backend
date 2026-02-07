package me.ultard.travo.security

import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID

fun currentUserIdOrNull(): UUID? {
    val auth = SecurityContextHolder.getContext().authentication
    return (auth?.principal as? UUID)
}

fun requireCurrentUserId(): UUID =
    currentUserIdOrNull() ?: throw IllegalStateException("Authentication required (missing or invalid JWT)")
