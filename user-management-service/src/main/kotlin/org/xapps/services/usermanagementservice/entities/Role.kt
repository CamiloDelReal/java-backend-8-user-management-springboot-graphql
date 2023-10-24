package org.xapps.services.usermanagementservice.entities

import jakarta.persistence.*

@Entity
@Table(name = "roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "value")
    var value: String
) {

    companion object {
        const val ADMINISTRATOR = "Administrator"
        const val GUEST = "Guest"
    }

}
