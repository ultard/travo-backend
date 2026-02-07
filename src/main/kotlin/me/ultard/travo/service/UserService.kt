package me.ultard.travo.service

import me.ultard.travo.dto.UserRegisterRequest
import me.ultard.travo.dto.UserResponse
import me.ultard.travo.domain.User
import me.ultard.travo.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun register(request: UserRegisterRequest): UserResponse {
        require(!userRepository.existsByEmail(request.email)) {
            "User with email ${request.email} already exists"
        }

        val password = request.password
        val passwordEncoded = passwordEncoder.encode(password)
            ?: throw IllegalArgumentException("Failed to encode password")

        val user = User(
            email = request.email,
            displayName = request.displayName,
            passwordHash = passwordEncoded,
        )

        val saved = userRepository.save(user)
        return toResponse(saved)
    }

    fun findById(id: UUID): User? = userRepository.findById(id).orElse(null)

    fun getById(id: UUID): User = findById(id) ?: throw NoSuchElementException("User not found: $id")

    fun login(email: String, password: String): User {
        val user = userRepository.findByEmail(email).orElseThrow {
            IllegalArgumentException("Invalid email or password")
        }
        require(passwordEncoder.matches(password, user.passwordHash)) {
            "Invalid email or password"
        }
        return user
    }

    fun toResponse(user: User) = UserResponse(
        id = user.id,
        email = user.email,
        displayName = user.displayName,
    )
}
