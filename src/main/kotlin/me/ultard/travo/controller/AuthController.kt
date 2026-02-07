package me.ultard.travo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.ultard.travo.dto.JwtLoginResponse
import me.ultard.travo.dto.UserLoginRequest
import me.ultard.travo.dto.UserRegisterRequest
import me.ultard.travo.dto.UserResponse
import me.ultard.travo.service.UserService
import me.ultard.travo.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "Авторизация")
class AuthController(
    private val userService: UserService,
    private val jwtService: JwtService,
) {

    @Operation(summary = "Регистрация", description = "Создаёт нового пользователя.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Пользователь создан"),
        ApiResponse(responseCode = "400", description = "Некорректные данные или email уже занят"),
    )
    @PostMapping("/register")
    fun register(@RequestBody request: UserRegisterRequest): ResponseEntity<UserResponse> {
        val user = userService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    @Operation(summary = "Вход", description = "Возвращает JWT и данные пользователя.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Успешный вход"),
        ApiResponse(responseCode = "400", description = "Неверный email или пароль"),
    )
    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): JwtLoginResponse {
        val user = userService.login(request.email, request.password)
        val token = jwtService.generateToken(user.id)
        return JwtLoginResponse(accessToken = token, user = userService.toResponse(user))
    }
}
