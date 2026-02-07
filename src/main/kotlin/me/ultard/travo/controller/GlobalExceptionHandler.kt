package me.ultard.travo.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.NoSuchElementException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (e.message ?: "Bad request")))

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(e: HttpMessageNotReadableException): ResponseEntity<Map<String, String>> {
        val message = e.mostSpecificCause?.message ?: e.message ?: "Invalid request body"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val message = e.bindingResult.fieldErrors.joinToString { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to message))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (e.message ?: "Not found")))

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(e: IllegalStateException): ResponseEntity<Map<String, String>> {
        if (e.message?.contains("Authentication required") == true) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to (e.message ?: "Unauthorized")))
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (e.message ?: "Bad request")))
    }
}
