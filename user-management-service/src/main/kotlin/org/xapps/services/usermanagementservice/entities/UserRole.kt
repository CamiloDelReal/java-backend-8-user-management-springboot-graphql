package org.xapps.services.usermanagementservice.entities

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "users_roles")
data class UserRole(
    @EmbeddedId
    var id: UserRoleId
) {

    @Embeddable
    data class UserRoleId(
        @Column(name = "user_id")
        var userId: Long,

        @Column(name = "role_id")
        var roleId: Long
    ) : Serializable

}
