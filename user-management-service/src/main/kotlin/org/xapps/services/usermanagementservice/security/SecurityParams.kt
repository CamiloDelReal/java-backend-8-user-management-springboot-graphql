package org.xapps.services.usermanagementservice.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security")
data class SecurityParams(
    val jwtGeneration: JwtGeneration
)

data class JwtGeneration(
    val type: String,
    val key: String,
    val validity: Long,
    val issuer: String
)