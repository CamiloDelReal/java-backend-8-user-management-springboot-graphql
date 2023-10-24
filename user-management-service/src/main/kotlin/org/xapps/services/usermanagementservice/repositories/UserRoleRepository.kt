package org.xapps.services.usermanagementservice.repositories

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.xapps.services.usermanagementservice.entities.UserRole


@Repository
interface UserRoleRepository: JpaRepository<UserRole, UserRole.UserRoleId> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM users_roles WHERE user_id = :userId", nativeQuery = true)
    fun deleteRolesByUserId(userId: Long?): Int?

}