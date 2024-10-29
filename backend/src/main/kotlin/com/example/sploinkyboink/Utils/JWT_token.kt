package com.example.sploinkyboink.Utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.stereotype.Component
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

import java.util.Date




@Component
class JWT_token (){

    private val jwtExpiration : Long = 0;
    private val jwtSecret : String = ""



    fun Application.configureSecurity(){
        install(Authentication) {
            jwt(){
                realm = ""
                 verifier(
                     JWT
                     .require(Algorithm.HMAC256(jwtSecret))
                         .withAudience()
                         .withIssuer()
                         .build()
                 )
            }

        }

    }

    fun generateToken(username : String, role : String) : String {

            val now = Date()
            val expiryDate = Date(now.time + jwtExpiration);

           val token = JWT.create()
                          .withAudience()
                          .withIssuer("something")
                          .withClaim("username", username)
                          .withExpiresAt(expiryDate)
                          .sign(Algorithm.HMAC256(jwtSecret))


            return token;

    }

}