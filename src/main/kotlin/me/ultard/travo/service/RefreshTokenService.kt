package me.ultard.travo.service

import me.ultard.travo.config.JwtProperties
import me.ultard.travo.domain.RefreshToken
import me.ultard.travo.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.util.Base64
import java.util.UUID

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
) {
    private val secureRandom = SecureRandom()

    fun issueForUser(userId: UUID, now: Instant = Instant.now()): String {
        val raw = generateOpaqueToken()
        val tokenHash = sha256Hex(raw)
        val expiresAt = now.plusMillis(jwtProperties.refreshExpirationMs)
        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                tokenHash = tokenHash,
                expiresAt = expiresAt,
            ),
        )
        return raw
    }

    @Transactional
    fun rotate(refreshTokenRaw: String, now: Instant = Instant.now()): UUID {
        val tokenHash = sha256Hex(refreshTokenRaw)
        val token = refreshTokenRepository.findByTokenHash(tokenHash)
            ?: throw IllegalArgumentException("Invalid refresh token")
        require(token.revokedAt == null) { "Refresh token revoked" }
        require(token.expiresAt.isAfter(now)) { "Refresh token expired" }

        token.revokedAt = now
        refreshTokenRepository.save(token)
        return token.userId
    }

    @Transactional
    fun logoutAllDevices(userId: UUID, now: Instant = Instant.now()) {
        refreshTokenRepository.revokeAllActiveByUserId(userId, now)
    }

    private fun generateOpaqueToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun sha256Hex(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}

