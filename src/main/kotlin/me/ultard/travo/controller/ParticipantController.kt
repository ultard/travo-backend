package me.ultard.travo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.ultard.travo.dto.ParticipantAddRequest
import me.ultard.travo.dto.ParticipantResponse
import me.ultard.travo.security.requireCurrentUserId
import me.ultard.travo.service.TripService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/trips/{tripId}/participants")
@Tag(name = "Участники поездки", description = "Управление участниками поездки.")
class ParticipantController(
    private val tripService: TripService,
) {

    @Operation(summary = "Список участников", description = "Все участники поездки с ролями.")
    @ApiResponse(responseCode = "200", description = "Список участников")
    @GetMapping
    fun list(@PathVariable tripId: UUID): List<ParticipantResponse> {
        requireCurrentUserId()
        return tripService.getParticipants(tripId)
    }

    @Operation(summary = "Добавить участника", description = "Добавляет пользователя в поездку. Доступно любому участнику поездки.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Участник добавлен"),
        ApiResponse(responseCode = "400", description = "Пользователь уже в поездке или не найден"),
        ApiResponse(responseCode = "404", description = "Поездка не найдена или нет доступа"),
    )
    @PostMapping
    fun add(@PathVariable tripId: UUID, @RequestBody request: ParticipantAddRequest): ResponseEntity<ParticipantResponse> {
        val userId = requireCurrentUserId()
        val participant = tripService.addParticipant(tripId, request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(participant)
    }

    @Operation(
        summary = "Удалить участника",
        description = "Исключает участника из поездки.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Участник удалён"),
        ApiResponse(responseCode = "403", description = "Нет прав: админ не может исключить владельца или другого админа"),
        ApiResponse(responseCode = "404", description = "Поездка или участник не найдены"),
    )
    @DeleteMapping("/{userId}")
    fun remove(@PathVariable tripId: UUID, @PathVariable userId: UUID): ResponseEntity<Unit> {
        val currentUserId = requireCurrentUserId()
        tripService.removeParticipant(tripId, userId, currentUserId)
        return ResponseEntity.noContent().build()
    }
}
