package org.xapps.services.usermanagementservice.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.xapps.services.usermanagementservice.repositories.RoleRepository


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfiguration @Autowired constructor(
    private val securityParams: SecurityParams,
    private val objectMapper: ObjectMapper,
    private val roleRepository: RoleRepository
) {

    @Bean
    fun providePasswordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(userDetailsService: UserDetailsService): AuthenticationManager =
        ProviderManager(listOf(
            DaoAuthenticationProvider().apply {
                setUserDetailsService(userDetailsService)
                setPasswordEncoder(providePasswordEncoder())
            }
        ))

    @Bean
    fun provideSecurityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain =
        http
            .cors { corsCustomizer ->
                corsCustomizer.disable()
            }
            .csrf { csrfCustomizer ->
                csrfCustomizer.disable()
            }
            .headers { headersCustomizer ->
                headersCustomizer.frameOptions { frameOptionCustomizer ->
                    frameOptionCustomizer.disable()
                }
            }
            .authorizeHttpRequests { authHttpReqCustomizer ->
                authHttpReqCustomizer
                    .requestMatchers(HttpMethod.POST, "/graphql").permitAll()
                    .requestMatchers("/graphiql").permitAll()
                    .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { sessionManagementCustomizer ->
                sessionManagementCustomizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(provideAuthorizationFilter(), BasicAuthenticationFilter::class.java)
            .build()

    fun provideAuthorizationFilter(): AuthorizationFilter =
        AuthorizationFilter(securityParams, objectMapper, roleRepository)

}