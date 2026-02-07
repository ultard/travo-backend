package me.ultard.travo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.ultard.travo.dto.ExpenseCreateRequest
import me.ultard.travo.dto.ExpenseCreateResponse
import me.ultard.travo.dto.ExpenseResponse
import me.ultard.travo.dto.ExpenseUpdateRequest
import me.ultard.travo.security.requireCurrentUserId
import me.ultard.travo.service.ExpenseService
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
@RequestMapping("/trips/{tripId}/expenses")
@Tag(name = "Траты", description = "Учёт трат по поездке и разбивка по участникам.")
class ExpenseController(
    private val expenseService: ExpenseService,
) {

    @Operation(summary = "Список трат", description = "Все траты поездки, отсортированные по дате.")
    @ApiResponse(responseCode = "200", description = "Список трат")
    @GetMapping
    fun list(@PathVariable tripId: UUID): List<ExpenseResponse> {
        val userId = requireCurrentUserId()
        return expenseService.listByTrip(tripId, userId)
    }

    @Operation(
        summary = "Добавить трату",
        description = "Создаёт трату. Сумма splits должна равняться amountCents. Участники в splits должны быть участниками поездки.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Трата создана"),
        ApiResponse(responseCode = "400", description = "Сумма splits не совпадает с amountCents"),
        ApiResponse(responseCode = "404", description = "Поездка не найдена или нет доступа"),
    )
    @PostMapping
    fun create(@PathVariable tripId: UUID, @RequestBody request: ExpenseCreateRequest): ResponseEntity<ExpenseCreateResponse> {
        val userId = requireCurrentUserId()
        val expense = expenseService.create(tripId, request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(expense)
    }

    @Operation(summary = "Получить трату", description = "Детали одной траты по id.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Трата найдена"),
        ApiResponse(responseCode = "404", description = "Трата или поездка не найдены"),
    )
    @GetMapping("/{expenseId}")
    fun get(@PathVariable tripId: UUID, @PathVariable expenseId: UUID): ExpenseResponse {
        val userId = requireCurrentUserId()
        return expenseService.getById(tripId, expenseId, userId)
    }

    @Operation(summary = "Обновить трату", description = "Изменение суммы, описания, категории или разбивки (splits). Сумма splits должна равняться amountCents.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Трата обновлена"),
        ApiResponse(responseCode = "400", description = "Некорректные данные"),
        ApiResponse(responseCode = "404", description = "Трата не найдена"),
    )
    @PutMapping("/{expenseId}")
    fun update(
        @PathVariable tripId: UUID,
        @PathVariable expenseId: UUID,
        @RequestBody request: ExpenseUpdateRequest,
    ): ExpenseResponse {
        val userId = requireCurrentUserId()
        return expenseService.update(tripId, expenseId, request, userId)
    }

    @Operation(summary = "Удалить трату", description = "Удаление траты. Доступно любому участнику поездки.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Трата удалена"),
        ApiResponse(responseCode = "404", description = "Трата не найдена"),
    )
    @DeleteMapping("/{expenseId}")
    fun delete(@PathVariable tripId: UUID, @PathVariable expenseId: UUID): ResponseEntity<Unit> {
        val userId = requireCurrentUserId()
        expenseService.delete(tripId, expenseId, userId)
        return ResponseEntity.noContent().build()
    }
}
