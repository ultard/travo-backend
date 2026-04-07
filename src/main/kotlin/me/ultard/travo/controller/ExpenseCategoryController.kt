package me.ultard.travo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.ultard.travo.dto.ExpenseCategoryCreateRequest
import me.ultard.travo.dto.ExpenseCategoryResponse
import me.ultard.travo.service.ExpenseCategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Категории трат", description = "Глобальный справочник категорий для трат (используйте code для локализации на клиенте).")
class ExpenseCategoryController(
    private val expenseCategoryService: ExpenseCategoryService,
) {
    @Operation(summary = "Список категорий трат")
    @ApiResponse(responseCode = "200", description = "Список категорий")
    @GetMapping("/expense-categories")
    fun list(): List<ExpenseCategoryResponse> {
        return expenseCategoryService.list()
    }

    @Operation(summary = "Создать категорию трат")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Категория создана"),
        ApiResponse(responseCode = "400", description = "Некорректные данные/дубликат"),
    )
    @PostMapping("/expense-categories")
    fun create(@RequestBody request: ExpenseCategoryCreateRequest): ResponseEntity<ExpenseCategoryResponse> {
        val created = expenseCategoryService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }
}

