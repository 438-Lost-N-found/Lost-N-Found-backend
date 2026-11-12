
package com.lostnfound.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/", "/reset-password.html", "/actuator/health").permitAll()
                it.requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                it.anyRequest().authenticated()


            }

        return http.build()


    }




}
