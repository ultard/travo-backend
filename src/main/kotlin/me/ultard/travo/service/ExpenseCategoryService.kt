package me.ultard.travo.service

import me.ultard.travo.domain.ExpenseCategory
import me.ultard.travo.dto.ExpenseCategoryCreateRequest
import me.ultard.travo.dto.ExpenseCategoryResponse
import me.ultard.travo.repository.ExpenseCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExpenseCategoryService(
    private val expenseCategoryRepository: ExpenseCategoryRepository,
) {
    @Transactional(readOnly = true)
    fun list(): List<ExpenseCategoryResponse> {
        return expenseCategoryRepository.findAllByOrderByCodeAsc().map(::toResponse)
    }

    @Transactional
    fun create(request: ExpenseCategoryCreateRequest): ExpenseCategoryResponse {
        val code = request.code.trim().uppercase()
        require(code.isNotBlank()) { "Category code is required" }
        require(code.length <= 64) { "Category code is too long" }
        require(code.matches(Regex("^[A-Z0-9_]+$"))) { "Category code must match ^[A-Z0-9_]+$" }
        require(!expenseCategoryRepository.existsByCodeIgnoreCase(code)) { "Category already exists" }
        val saved = expenseCategoryRepository.save(ExpenseCategory(code = code))
        return toResponse(saved)
    }

    private fun toResponse(c: ExpenseCategory) = ExpenseCategoryResponse(
        id = c.id,
        code = c.code,
        createdAt = c.createdAt,
    )
}

