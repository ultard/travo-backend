package me.ultard.travo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.ultard.travo.dto.RoutePointCreateRequest
import me.ultard.travo.dto.RoutePointResponse
import me.ultard.travo.dto.RoutePointUpdateRequest
import me.ultard.travo.security.requireCurrentUserId
import me.ultard.travo.service.RouteService
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
@RequestMapping("/trips/{tripId}/routes")
@Tag(name = "Маршрут", description = "Планирование пунктов маршрута поездки (остановки, точки).")
class RouteController(
    private val routeService: RouteService,
) {

    @Operation(summary = "Список пунктов маршрута", description = "Все точки маршрута по порядку position.")
    @ApiResponse(responseCode = "200", description = "Список пунктов")
    @GetMapping
    fun list(@PathVariable tripId: UUID): List<RoutePointResponse> {
        val userId = requireCurrentUserId()
        return routeService.list(tripId, userId)
    }

    @Operation(summary = "Добавить пункт маршрута")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Пункт добавлен"),
        ApiResponse(responseCode = "404", description = "Поездка не найдена"),
    )
    @PostMapping
    fun create(
        @PathVariable tripId: UUID,
        @RequestBody request: RoutePointCreateRequest,
    ): ResponseEntity<RoutePointResponse> {
        val userId = requireCurrentUserId()
        val route = routeService.create(tripId, request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(route)
    }

    @Operation(summary = "Изменить пункт маршрута")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Пункт обновлён"),
        ApiResponse(responseCode = "404", description = "Пункт не найден"),
    )
    @PutMapping("/{routeId}")
    fun update(
        @PathVariable tripId: UUID,
        @PathVariable routeId: UUID,
        @RequestBody request: RoutePointUpdateRequest,
    ): RoutePointResponse {
        val userId = requireCurrentUserId()
        return routeService.update(tripId, routeId, request, userId)
    }

    @Operation(summary = "Удалить пункт маршрута")
    @ApiResponse(responseCode = "204", description = "Пункт удалён")
    @DeleteMapping("/{routeId}")
    fun delete(
        @PathVariable tripId: UUID,
        @PathVariable routeId: UUID,
    ): ResponseEntity<Unit> {
        val userId = requireCurrentUserId()
        routeService.delete(tripId, routeId, userId)
        return ResponseEntity.noContent().build()
    }
}
