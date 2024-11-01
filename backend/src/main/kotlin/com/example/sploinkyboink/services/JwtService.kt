package com.example.sploinkyboink.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import java.util.Date
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value


@Service
class JwtService {

    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long = 3600000

    @Value("\${jwt.secret}")
    private lateinit var jwtSecret : String

    @Value("\${jwt.issuer}")
    private lateinit var issuer : String
    private val verifier by lazy {
        JWT.require(Algorithm.HMAC256(jwtSecret))
            .withIssuer(issuer)
            .build()
    }
    // Validate the token and handle exceptions
    fun validateToken(token: String): Boolean {
        return try {
            val decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
                .withIssuer(issuer)
                .build()
                .verify(token)

            !decodedJWT.expiresAt.before(Date())
        } catch (ex: JWTVerificationException) {
            false
        }
    }

    // Generate a new token with username claim and expiration
    fun generateToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration)

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(username)
            .withExpiresAt(expiryDate)
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    // Check if the user is logged in based on JWT cookie
    fun isLoggedIn(request: HttpServletRequest): Boolean {
        val cookies = request.cookies ?: return false
        val jwtToken = cookies.firstOrNull { it.name == "jwt" }?.value
        return jwtToken != null && validateToken(jwtToken)
    }

    fun getUsernameFromToken(token: String): String? {
        return try {
            val decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
                .withIssuer(issuer)
                .build()
                .verify(token)
            decodedJWT.subject
        } catch (ex: JWTVerificationException) {
            null
        }
    }
}
