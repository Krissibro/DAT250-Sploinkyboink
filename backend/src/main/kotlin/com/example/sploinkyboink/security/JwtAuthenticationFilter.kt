package com.example.sploinkyboink.security

import com.example.sploinkyboink.services.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
    ) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val jwtCookie : Cookie? =  request.cookies?.find { it.name == "jwt"}
        if (jwtCookie != null) {
            val token = jwtCookie.value
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
