package org.xapps.services.usermanagementservice.dtos

import org.xapps.services.usermanagementservice.entities.Role

data class AuthenticatedUser (
    val id: Long,
    val name: String,
    val lastname: String,
    val email: String,
    val roles: List<Role>
)