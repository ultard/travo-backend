package me.ultard.travo.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.NoSuchElementException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(e: IllegalStateException): ResponseEntity<Map<String, String>> {
        if (e.message?.contains("Authentication required") == true) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to (e.message ?: "Unauthorized")))
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (e.message ?: "Bad request")))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (e.message ?: "Not found")))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (e.message ?: "Bad request")))
}
