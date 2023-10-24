package org.xapps.services.usermanagementservice.dtos

data class Authorization (
    val type: String,
    val token: String,
    val expiration: Long
)