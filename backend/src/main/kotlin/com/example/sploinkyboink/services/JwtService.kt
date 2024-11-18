package com.example.sploinkyboink.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.stereotype.Service
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


@Service
class JwtService {

    private val log : Logger = LoggerFactory.getLogger(JwtService::class.java)

    @Value("\${jwt.issuer}")
    private lateinit var issuer : String

    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long = 0

    private var jwtSecret : String = ""

    init {
        try {
            val keyGen = KeyGenerator.getInstance("HmacSHA256")
            val secretKey: SecretKey = keyGen.generateKey()
            jwtSecret = Base64.getEncoder().encodeToString(secretKey.encoded)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }


    fun validateToken(token: String): Boolean {
        try {
           val decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
               .withIssuer(issuer)
               .build()
               .verify(token)

          return !decodedJWT.expiresAt.before(Date())
       }catch (ex: JWTVerificationException){
           log.error("Could not validate token: {}", token, ex)
           return false
       }

    }

    fun generateToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration)

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(username)
            .withExpiresAt(expiryDate)
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    fun getUsernameFromToken(token: String): String? {
          try {
            val decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
                .withIssuer(issuer)
                .build()
                .verify(token)
            return decodedJWT.subject
        } catch (ex: JWTVerificationException) {
            log.error("Could not extract username from token: {}", token, ex)
            return null
        }
    }

}
