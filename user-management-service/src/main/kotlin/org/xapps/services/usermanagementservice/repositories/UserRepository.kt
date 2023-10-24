package org.xapps.services.usermanagementservice.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.xapps.services.usermanagementservice.entities.User

@Repository
interface UserRepository: JpaRepository<User, Long> {

    fun findByEmail(email: String): User?

    fun findByIdNotAndEmail(id: Long, email: String): User?

}