package org.xapps.services.usermanagementservice.entities

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "name")
    var name: String,

    @Column(name = "lastname")
    var lastname: String,

    @Column(name = "email")
    var email: String,

    @Column(name = "password_protected")
    var passwordProtected: String
)
