package me.ultard.travo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.ultard.travo.dto.TripInviteCreateRequest
import me.ultard.travo.dto.TripInviteResponse
import me.ultard.travo.security.requireCurrentUserId
import me.ultard.travo.service.InviteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Инвайты", description = "Приглашения в поездки по email.")
class InviteController(
    private val inviteService: InviteService,
) {
    @Operation(summary = "Создать инвайт в поездку")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Инвайт создан"),
        ApiResponse(responseCode = "403", description = "Нет прав"),
        ApiResponse(responseCode = "404", description = "Поездка не найдена или нет доступа"),
    )
    @PostMapping("/trips/{tripId}/invites")
    fun create(@PathVariable tripId: UUID, @RequestBody request: TripInviteCreateRequest): ResponseEntity<TripInviteResponse> {
        val userId = requireCurrentUserId()
        val invite = inviteService.create(tripId, request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(invite)
    }

    @Operation(summary = "Мои инвайты", description = "Список ожидающих инвайтов для текущего пользователя (по email).")
    @ApiResponse(responseCode = "200", description = "Список инвайтов")
    @GetMapping("/invites")
    fun listMy(): List<TripInviteResponse> {
        val userId = requireCurrentUserId()
        return inviteService.listMyInvites(userId)
    }

    @Operation(summary = "Принять инвайт")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Инвайт принят"),
        ApiResponse(responseCode = "400", description = "Инвайт некорректен/истёк"),
        ApiResponse(responseCode = "404", description = "Инвайт не найден"),
    )
    @PostMapping("/invites/{inviteId}/accept")
    fun accept(@PathVariable inviteId: UUID): ResponseEntity<Unit> {
        val userId = requireCurrentUserId()
        inviteService.accept(inviteId, userId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Отклонить инвайт")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Инвайт отклонён"),
        ApiResponse(responseCode = "404", description = "Инвайт не найден"),
    )
    @PostMapping("/invites/{inviteId}/decline")
    fun decline(@PathVariable inviteId: UUID): ResponseEntity<Unit> {
        val userId = requireCurrentUserId()
        inviteService.decline(inviteId, userId)
        return ResponseEntity.noContent().build()
    }
}

