package me.ultard.travo.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import me.ultard.travo.config.JwtProperties
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtProperties: JwtProperties,
) {

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.encodeToByteArray())
    }

    fun generateToken(userId: UUID): String =
        Jwts.builder()
            .subject(userId.toString())
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + jwtProperties.expirationMs))
            .signWith(secretKey)
            .compact()

    fun parseUserId(token: String): UUID? =
        try {
            val claims: Claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
            UUID.fromString(claims.subject)
        } catch (_: ExpiredJwtException) {
            null
        } catch (_: Exception) {
            null
        }
}
