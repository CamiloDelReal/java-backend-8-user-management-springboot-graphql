package org.xapps.services.usermanagementservice.services

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.xapps.services.usermanagementservice.dtos.*
import org.xapps.services.usermanagementservice.entities.Role
import org.xapps.services.usermanagementservice.entities.User
import org.xapps.services.usermanagementservice.entities.UserRole
import org.xapps.services.usermanagementservice.entities.UserRole.UserRoleId
import org.xapps.services.usermanagementservice.repositories.RoleRepository
import org.xapps.services.usermanagementservice.repositories.UserRepository
import org.xapps.services.usermanagementservice.repositories.UserRoleRepository
import org.xapps.services.usermanagementservice.security.SecurityParams
import org.xapps.services.usermanagementservice.services.exceptions.DuplicityException
import org.xapps.services.usermanagementservice.services.exceptions.InvalidCredentialException
import org.xapps.services.usermanagementservice.services.exceptions.UserNotFoundException
import java.time.Instant
import java.util.*


@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val userRoleRepository: UserRoleRepository,
    private val authenticationManager: AuthenticationManager,
    private val securityParams: SecurityParams,
    private val objectMapper: ObjectMapper,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(credential: Login): Authorization {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(credential.email, credential.password)
        )
        return userRepository.findByEmail(credential.email)?.let { user ->
            user.passwordProtected = ""
            val currentTimestamp = Instant.now().toEpochMilli()
            val expirationRaw = currentTimestamp + securityParams.jwtGeneration.validity
            val expiration = Date(expirationRaw)
            val subject = objectMapper.writeValueAsString(user)
            val id = UUID.randomUUID().toString()
            val token = Jwts.builder()
                .subject(subject)
                .id(id)
                .issuedAt(Date(currentTimestamp))
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(securityParams.jwtGeneration.key.toByteArray()))
                .compact()
            Authorization(securityParams.jwtGeneration.type, token, expirationRaw)
        } ?: run {
            throw InvalidCredentialException("Bad credentials")
        }
    }

    fun readAllUsers(): List<User> =
        userRepository.findAll().onEach { it.passwordProtected = "" }

    fun readUser(id: Long): User? =
        userRepository.findById(id).orElse(null).apply { passwordProtected = "" }
            ?: throw UserNotFoundException("User with id=$id not found")

    fun hasAdminRole(userRequest: UserCreateRequest): Boolean {
        return if (userRequest.roles != null && userRequest.roles!!.isNotEmpty()) {
            val administratorRole = roleRepository.findByValue(Role.ADMINISTRATOR)
            administratorRole != null && userRequest.roles!!.stream()
                .anyMatch { value -> value == administratorRole.value }
        } else {
            false
        }
    }

    fun hasAdminRole(userRequest: UserUpdateRequest): Boolean {
        return if (userRequest.roles != null && userRequest.roles!!.isNotEmpty()) {
            val administratorRole = roleRepository.findByValue(Role.ADMINISTRATOR)
            administratorRole != null && userRequest.roles!!.stream()
                .anyMatch { value -> value == administratorRole.value }
        } else {
            false
        }
    }

    fun hasAdminRole(user: AuthenticatedUser): Boolean {
        return if (user.roles.isNotEmpty()) {
            val administratorRole = roleRepository.findByValue(Role.ADMINISTRATOR)
            administratorRole != null && user.roles.stream().anyMatch { role -> role.id == administratorRole.id }
        } else {
            false
        }
    }

    fun createUser(request: UserCreateRequest): User {
        val duplicity = userRepository.findByEmail(request.email)
        return duplicity?.let {
            throw DuplicityException("Email " + request.email + " is not available")
        } ?: run {
            val user = User(
                name = request.name,
                lastname = request.lastname,
                email = request.email,
                passwordProtected = passwordEncoder.encode(request.password)
            )
            var roles: List<Role>? = null
            if (!request.roles.isNullOrEmpty()) {
                roles = roleRepository.findByValues(request.roles!!)
            }
            if (roles.isNullOrEmpty()) {
                val guestRole = roleRepository.findByValue(Role.GUEST)
                roles = listOf(guestRole!!)
            }
            userRepository.save(user)
            val userRoles = roles.map {
                UserRole(id = UserRole.UserRoleId(userId = user.id, roleId = it.id))
            }
            userRoleRepository.saveAll(userRoles)
            user.passwordProtected = ""
            user
        }
    }

    fun updateUser(id: Long, request: UserUpdateRequest): User {
        val userContainer = userRepository.findById(id)
        return if (userContainer.isPresent) {
            if (request.email != null) {
                val duplicity = userRepository.findByIdNotAndEmail(id, request.email!!)
                duplicity?.let {
                    throw DuplicityException("Email=${request.email} is not available")
                }
            }

            val user = userContainer.get()
            request.name?.let { user.name = it }
            request.lastname?.let { user.lastname = it }
            request.email?.let { user.email = it }
            request.password?.let { user.passwordProtected = passwordEncoder.encode(it) }

            if (request.roles != null && request.roles!!.isNotEmpty()) {
                val roles = roleRepository.findByValues(request.roles!!)
                if (roles.isNotEmpty()) {
                    userRoleRepository.deleteRolesByUserId(user.id)
                    userRoleRepository.saveAll(roles.map { role: Role -> UserRole(UserRoleId(user.id, role.id)) })
                }
            }
            userRepository.save(user)
            user.passwordProtected = ""
            user
        } else {
            throw UserNotFoundException("Nonexistent user with id=$id")
        }
    }

    fun delete(id: Long) {
        val userContainer = userRepository.findById(id)
        if (userContainer.isPresent) {
            userRoleRepository.deleteRolesByUserId(userContainer.get().id)
            userRepository.delete(userContainer.get())
        } else {
            throw UserNotFoundException("Nonexistent user with id=$id")
        }
    }

}