package org.xapps.services.usermanagementservice.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.xapps.services.usermanagementservice.dtos.*
import org.xapps.services.usermanagementservice.entities.Role
import org.xapps.services.usermanagementservice.entities.User
import org.xapps.services.usermanagementservice.services.UserService
import org.xapps.services.usermanagementservice.services.exceptions.ForbiddenException


@Controller
class UserController @Autowired constructor(
    private val userService: UserService
) {

    @MutationMapping
    fun login(@Argument credential: Login): Authorization =
        userService.login(credential)

    @PreAuthorize("isAuthenticated() and hasAuthority('${Role.ADMINISTRATOR}')")
    @QueryMapping
    fun getUsers(): List<User> =
        userService.readAllUsers()

    @QueryMapping
    fun getUser(@Argument id: Long): User? =
        userService.readUser(id)

    @MutationMapping
    fun createUser(@Argument request: UserCreateRequest): User {
        val requestingCreateAdmin: Boolean = userService.hasAdminRole(request)
        val principal = SecurityContextHolder.getContext().authentication.principal
        return if (!requestingCreateAdmin || principal != null && principal is AuthenticatedUser && userService.hasAdminRole(principal)) {
            userService.createUser(request)
        } else {
            throw ForbiddenException("Authenticated user does not have permission for the requested operation")
        }
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.id == #id")
    @MutationMapping
    fun updateUser(@Argument id: Long, @Argument request: UserUpdateRequest): User {
        val requestingUpdateToAdmin = userService.hasAdminRole(request)
        val principal = SecurityContextHolder.getContext().authentication.principal
        return if (!requestingUpdateToAdmin || principal != null && principal is AuthenticatedUser && userService.hasAdminRole(principal)) {
            userService.updateUser(id, request)
        } else {
            throw ForbiddenException("Authenticated user does not have permission for the requested operation")
        }
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.id == #id")
    @MutationMapping
    fun deleteUser(@Argument id: Long): Boolean {
        userService.delete(id)
        return true
    }

}