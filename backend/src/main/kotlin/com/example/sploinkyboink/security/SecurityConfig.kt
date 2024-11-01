package com.example.sploinkyboink.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import com.example.sploinkyboink.security.JwtAuthenticationFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig (private val jwtAuthenticationFilter: JwtAuthenticationFilter) {


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }  // Disable CSRF for stateless authentication
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/sploinkyboinkend/login", "/sploinkyboinkend/users","/sploinkyboinkend/logout" ).permitAll() // Allow these endpoints without authentication
                    .anyRequest().authenticated() // Require authentication for any other request
            }

        return http.build()
    }

}