package org.xapps.services.usermanagementservice.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.xapps.services.usermanagementservice.entities.Role

@Repository
interface RoleRepository: JpaRepository<Role, Long> {

    @Query(
        value = "SELECT roles.* FROM roles, users_roles WHERE :userId = users_roles.user_id AND users_roles.role_id = roles.id",
        nativeQuery = true)
    fun findRolesByUserId(userId: Long): List<Role>

    fun findByValue(value: String): Role?

    @Query(value = "SELECT * FROM roles WHERE value IN :values", nativeQuery = true)
    fun findByValues(values: List<String>): List<Role>
}