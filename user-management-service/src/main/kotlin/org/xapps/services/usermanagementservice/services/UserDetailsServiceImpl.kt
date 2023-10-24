package org.xapps.services.usermanagementservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.xapps.services.usermanagementservice.repositories.RoleRepository
import org.xapps.services.usermanagementservice.repositories.UserRepository
import org.xapps.services.usermanagementservice.services.exceptions.UserNotFoundException

@Service
class UserDetailsServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByEmail(username)?.let { systemUser ->
            User(
                systemUser.email,
                systemUser.passwordProtected,
                true,
                true,
                true,
                true,
                roleRepository.findRolesByUserId(systemUser.id).map { SimpleGrantedAuthority(it.value) })
        } ?: run {
            throw UserNotFoundException("Bad credentials")
        }
    }

}