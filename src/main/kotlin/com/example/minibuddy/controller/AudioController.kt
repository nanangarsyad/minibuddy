package com.example.minibuddy.controller

import com.example.minibuddy.service.AudioService
import kotlinx.coroutines.reactor.mono
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/audio")
class AudioController(private val audioService: AudioService) {

    @PostMapping("/user/{userId}/phrase/{phraseId}")
    fun uploadAudio(
        @PathVariable userId: Long,
        @PathVariable phraseId: Long,
        @RequestPart("audio_file") filePart: FilePart
    ): Mono<String> = mono {
        val saved = audioService.storeAudio(userId, phraseId, filePart)
        "Audio stored with id: ${saved.id}"
    }

    @GetMapping(
        "/user/{userId}/phrase/{phraseId}/{audioFormat}",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun getAudio(
        @PathVariable userId: Long,
        @PathVariable phraseId: Long,
        @PathVariable audioFormat: String
    ): Flux<DataBuffer> = mono {
        audioService.retrieveAudioStream(userId, phraseId, audioFormat)
    }.flatMapMany { it }
}
