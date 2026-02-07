package me.ultard.travo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import me.ultard.travo.dto.SettlementResponse
import me.ultard.travo.security.requireCurrentUserId
import me.ultard.travo.service.SettlementService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/trips/{tripId}/settlements")
@Tag(name = "Расчёты (долги)", description = "Кто кому сколько должен по итогам трат.")
class SettlementController(
    private val settlementService: SettlementService,
) {

    @Operation(
        summary = "Список долгов",
        description = "Минимальный набор переводов: кто (fromUserId) кому (toUserId) сколько (amountCents) должен. По валюте поездки.",
    )
    @ApiResponse(responseCode = "200", description = "Список долгов между участниками")
    @GetMapping
    fun list(@PathVariable tripId: UUID): List<SettlementResponse> {
        val userId = requireCurrentUserId()
        return settlementService.getSettlements(tripId, userId)
    }
}
