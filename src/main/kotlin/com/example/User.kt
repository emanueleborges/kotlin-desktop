package com.example

import java.time.LocalDateTime

/**
 * Model class for User
 */
data class User(
    val id: Long = 0,
    val name: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime? = null
)
