package org.xapps.services.usermanagementservice.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.xapps.services.usermanagementservice.dtos.AuthenticatedUser
import org.xapps.services.usermanagementservice.entities.User
import org.xapps.services.usermanagementservice.repositories.RoleRepository
import org.xapps.services.usermanagementservice.services.exceptions.InvalidCredentialException
import javax.crypto.spec.SecretKeySpec


class AuthorizationFilter(
    private val securityParams: SecurityParams,
    private val objectMapper: ObjectMapper,
    private val roleRepository: RoleRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authzHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authzHeader != null && authzHeader.startsWith(securityParams.jwtGeneration.type)) {
            val authentication = getAuthentication(authzHeader.removePrefix(securityParams.jwtGeneration.type).trim())
            if (authentication != null) {
                SecurityContextHolder.getContext().authentication = authentication
            } else {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun getAuthentication(token: String): UsernamePasswordAuthenticationToken? {
        var authentication: UsernamePasswordAuthenticationToken? = null

        try {
            val claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(securityParams.jwtGeneration.key.toByteArray()))
                .build()
                .parseSignedClaims(token)
                .payload
            val subject = claims.subject
            val user = objectMapper.readValue(subject, User::class.java)
            val roles = roleRepository.findRolesByUserId(user.id)
            val authenticatedUser = AuthenticatedUser(
                id = user.id,
                name = user.name,
                lastname = user.lastname,
                email = user.email,
                roles = roles
            )
            val authorities = roles.map { SimpleGrantedAuthority(it.value) }
            authentication = UsernamePasswordAuthenticationToken(authenticatedUser, null, authorities)

        } catch (ex: Exception) {
            throw InvalidCredentialException("Credential is no longer valid")
        }

        return authentication
    }

}