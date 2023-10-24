package org.xapps.services.usermanagementservice.dtos

data class UserCreateRequest(
    var name: String,
    var lastname: String,
    var email: String,
    var password: String,
    var roles: List<String>? = null
)