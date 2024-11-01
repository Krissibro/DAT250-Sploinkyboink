package com.example.sploinkyboink.security

import com.auth0.jwt.JWT
import com.example.sploinkyboink.services.JwtService
import com.example.sploinkyboink.services.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userService: UserService
    ) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7) // Remove "Bearer "
            if (jwtService.validateToken(token)) {
                val username = jwtService.getUsernameFromToken(token)
                if (username != null) {
                    val auth = UsernamePasswordAuthenticationToken(username, null, null)
                    SecurityContextHolder.getContext().authentication = auth
                }
            }
        }
        chain.doFilter(request, response)
    }
}
