package me.ultard.travo.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.util.UUID

class UserIdAuthentication(private val userId: UUID) : Authentication {

    override fun getPrincipal(): UUID = userId
    override fun getName(): String = userId.toString()
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()
    override fun getCredentials(): Any? = null
    override fun getDetails(): Any? = null
    override fun isAuthenticated(): Boolean = true
    override fun setAuthenticated(isAuthenticated: Boolean) {}
}
