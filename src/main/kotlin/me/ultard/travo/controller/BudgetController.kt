package me.ultard.travo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.ultard.travo.dto.TripBudgetResponse
import me.ultard.travo.dto.TripBudgetUpsertRequest
import me.ultard.travo.security.requireCurrentUserId
import me.ultard.travo.service.BudgetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Tag(name = "Бюджеты", description = "Лимиты/бюджеты по поездке и (опционально) по категориям.")
class BudgetController(
    private val budgetService: BudgetService,
) {
    @Operation(summary = "Список бюджетов поездки")
    @ApiResponse(responseCode = "200", description = "Список бюджетов")
    @GetMapping("/trips/{tripId}/budgets")
    fun list(@PathVariable tripId: UUID): List<TripBudgetResponse> {
        val userId = requireCurrentUserId()
        return budgetService.list(tripId, userId)
    }

    @Operation(summary = "Создать/обновить бюджет", description = "Upsert по паре (tripId, category). category=null — общий бюджет поездки.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Бюджет создан/обновлён"),
        ApiResponse(responseCode = "400", description = "Некорректные данные"),
    )
    @PutMapping("/trips/{tripId}/budgets")
    fun upsert(@PathVariable tripId: UUID, @RequestBody request: TripBudgetUpsertRequest): TripBudgetResponse {
        val userId = requireCurrentUserId()
        return budgetService.upsert(tripId, request, userId)
    }

    @Operation(summary = "Удалить бюджет")
    @ApiResponse(responseCode = "204", description = "Бюджет удалён")
    @DeleteMapping("/trips/{tripId}/budgets/{budgetId}")
    fun delete(@PathVariable tripId: UUID, @PathVariable budgetId: UUID): ResponseEntity<Unit> {
        val userId = requireCurrentUserId()
        budgetService.delete(tripId, budgetId, userId)
        return ResponseEntity.noContent().build()
    }
}

