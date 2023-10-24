package org.xapps.services.usermanagementservice.seeders

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.xapps.services.usermanagementservice.entities.Role
import org.xapps.services.usermanagementservice.entities.User
import org.xapps.services.usermanagementservice.entities.UserRole
import org.xapps.services.usermanagementservice.repositories.RoleRepository
import org.xapps.services.usermanagementservice.repositories.UserRepository
import org.xapps.services.usermanagementservice.repositories.UserRoleRepository

@Component
class DatabaseSeeder @Autowired constructor(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @EventListener
    fun seed(event: ContextRefreshedEvent) {
        var administratorRole: Role? = null
        if (roleRepository.count() == 0L) {
            val guestRole = Role(value = Role.GUEST)
            administratorRole = Role(value = Role.ADMINISTRATOR)
            roleRepository.saveAll(listOf(guestRole, administratorRole))
        }
        if (userRepository.count() == 0L) {
            val administratorUser = User(
                name = "Root",
                lastname = "from Heaven",
                email = "root@gmail.com",
                passwordProtected = passwordEncoder.encode("123456")
            )
            userRepository.save(administratorUser)
            userRoleRepository.save(
                UserRole(
                    id = UserRole.UserRoleId(
                        userId = administratorUser.id,
                        roleId = administratorRole!!.id
                    )
                )
            )

        }
    }

}