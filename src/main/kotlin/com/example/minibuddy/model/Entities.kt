package com.example.minibuddy.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class User(
    @Id val id: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Table("phrases")
data class Phrase(
    @Id val id: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
