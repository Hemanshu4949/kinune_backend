package com.Kizuna.backend.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var phoneNumber: String = "",

    @Column(nullable = false)
    var displayName: String = "",

    @Column(nullable = true)
    var avatarUrl: String? = null,

    @Column(nullable = false)
    var isOnline: Boolean = false
)
