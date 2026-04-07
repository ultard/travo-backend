package me.ultard.travo.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String = "",
    val expirationMs: Long = 86400000,
    val refreshExpirationMs: Long = 2592000000,
)
