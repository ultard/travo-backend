package me.ultard.travo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.ultard.travo.dto.TripCreateRequest
import me.ultard.travo.dto.TripCreateResponse
import me.ultard.travo.dto.TripResponse
import me.ultard.travo.dto.TripUpdateRequest
import me.ultard.travo.security.requireCurrentUserId
import me.ultard.travo.service.TripService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/trips")
@Tag(name = "Поездки", description = "Создание и управление поездками.")
class TripController(
    private val tripService: TripService,
) {

    @Operation(summary = "Список поездок", description = "Все поездки, в которых участвует текущий пользователь.")
    @ApiResponse(responseCode = "200", description = "Список поездок")
    @GetMapping
    fun list(): List<TripResponse> {
        val userId = requireCurrentUserId()
        return tripService.listByUser(userId)
    }

    @Operation(summary = "Создать поездку")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Поездка создана"),
        ApiResponse(responseCode = "401", description = "Требуется авторизация"),
    )
    @PostMapping
    fun create(@RequestBody request: TripCreateRequest): ResponseEntity<TripCreateResponse> {
        val userId = requireCurrentUserId()
        val trip = tripService.create(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(trip)
    }

    @Operation(summary = "Получить поездку", description = "Детали поездки по id.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Поездка найдена"),
        ApiResponse(responseCode = "404", description = "Поездка не найдена или нет доступа"),
    )
    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): TripResponse {
        val userId = requireCurrentUserId()
        return tripService.get(id, userId)
    }

    @Operation(summary = "Обновить поездку", description = "Изменение названия, дат, валюты.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Поездка обновлена"),
        ApiResponse(responseCode = "404", description = "Поездка не найдена или нет доступа"),
    )
    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody request: TripUpdateRequest): TripResponse {
        val userId = requireCurrentUserId()
        return tripService.update(id, request, userId)
    }

    @Operation(summary = "Удалить поездку")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Поездка удалена"),
        ApiResponse(responseCode = "403", description = "Только владелец может удалить поездку"),
        ApiResponse(responseCode = "404", description = "Поездка не найдена"),
    )
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Unit> {
        val userId = requireCurrentUserId()
        tripService.delete(id, userId)
        return ResponseEntity.noContent().build()
    }
}
