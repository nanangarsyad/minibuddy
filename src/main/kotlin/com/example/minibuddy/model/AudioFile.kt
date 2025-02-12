package com.example.minibuddy.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("audio_files")
data class AudioFile(
    @Id
    val id: Long? = null,

    @Column("user_id")
    val userId: Long,

    @Column("phrase_id")
    val phraseId: Long,

    @Column("file_path")
    val filePath: String,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
