package com.example.minibuddy.repository

import com.example.minibuddy.model.AudioFile
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface AudioFileRepository : ReactiveCrudRepository<AudioFile, Long> {
    fun findByUserIdAndPhraseId(userId: Long, phraseId: Long): Mono<AudioFile>
}
