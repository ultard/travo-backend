package me.ultard.travo.config

import me.ultard.travo.security.JwtAuthFilter
import me.ultard.travo.security.JwtService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.http.HttpStatus
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.HttpStatusEntryPoint

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig {

    @Bean
    fun jwtAuthFilter(jwtService: JwtService): JwtAuthFilter =
        JwtAuthFilter(jwtService)

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthFilter: JwtAuthFilter,
    ): SecurityFilterChain {
        http {
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthFilter)
            exceptionHandling {
                authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            }
            authorizeHttpRequests {
                authorize("/auth/register", permitAll)
                authorize("/auth/login", permitAll)
                authorize("/scalar", permitAll)
                authorize("/scalar/**", permitAll)
                authorize("/v3/api-docs", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize(anyRequest, authenticated)
            }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
