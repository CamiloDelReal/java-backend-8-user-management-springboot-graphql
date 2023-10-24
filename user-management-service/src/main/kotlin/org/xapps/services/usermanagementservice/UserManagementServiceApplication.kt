package org.xapps.services.usermanagementservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.xapps.services.usermanagementservice.security.SecurityParams

@SpringBootApplication
@EnableConfigurationProperties(
    SecurityParams::class
)
class UserManagementServiceApplication

fun main(args: Array<String>) {
    runApplication<UserManagementServiceApplication>(*args)
}
