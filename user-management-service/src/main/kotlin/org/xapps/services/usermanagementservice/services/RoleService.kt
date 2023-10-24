package org.xapps.services.usermanagementservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.xapps.services.usermanagementservice.entities.Role
import org.xapps.services.usermanagementservice.repositories.RoleRepository
import org.xapps.services.usermanagementservice.services.exceptions.RoleNotFoundException

@Service
class RoleService @Autowired constructor(
    private val roleRepository: RoleRepository
) {

    fun readAllRoles(): List<Role> =
        roleRepository.findAll()

    fun readRole(id: Long): Role =
        roleRepository.findById(id).orElse(null) ?: throw RoleNotFoundException("Role with id=$id not found")

    fun readRolesByUserId(userId: Long): List<Role> =
        roleRepository.findRolesByUserId(userId)

}