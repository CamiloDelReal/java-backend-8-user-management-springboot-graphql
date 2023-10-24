package org.xapps.services.usermanagementservice.dtos

data class UserUpdateRequest(
    var name: String? = null,
    var lastname: String? = null,
    var email: String? = null,
    var password: String? = null,
    var roles: List<String>? = null
)