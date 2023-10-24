package org.xapps.services.usermanagementservice.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import org.xapps.services.usermanagementservice.entities.Role
import org.xapps.services.usermanagementservice.entities.User
import org.xapps.services.usermanagementservice.services.RoleService

@Controller
class RoleController @Autowired constructor(
    private val roleService: RoleService
) {

    @QueryMapping
    fun getRoles(): List<Role> =
        roleService.readAllRoles()

    @QueryMapping
    fun getRole(@Argument id: Long): Role =
        roleService.readRole(id)

    @SchemaMapping
    fun roles(user: User): List<Role> =
        roleService.readRolesByUserId(user.id)

}