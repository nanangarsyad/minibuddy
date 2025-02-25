package com.example.minibuddy.service

import com.example.minibuddy.exception.AudioFileNotFoundException
import com.example.minibuddy.model.AudioFile
import com.example.minibuddy.repository.AudioFileRepository
import com.example.minibuddy.repository.UserRepository
import com.example.minibuddy.repository.PhraseRepository
import com.example.minibuddy.exception.InvalidUserOrPhraseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.io.File

@Service
class AudioService(
    private val audioFileRepository: AudioFileRepository,
    private val storageService: StorageService,
    private val audioConversionService: AudioConversionService,
    private val userRepository: UserRepository,
    private val phraseRepository: PhraseRepository,
) {
    private val conversionDispatcher = Dispatchers.Default

    private suspend fun validateUserAndPhrase(userId: Long, phraseId: Long) {
        val userExists = userRepository.existsById(userId).awaitSingle()
        val phraseExists = phraseRepository.existsById(phraseId).awaitSingle()
        
        if (!userExists || !phraseExists) {
            throw InvalidUserOrPhraseException("Invalid user ID ($userId) or phrase ID ($phraseId)")
        }
    }

    suspend fun storeAudio(userId: Long, phraseId: Long, filePart: FilePart): AudioFile {
        validateUserAndPhrase(userId, phraseId)
        val tempFilename = "${userId}_${phraseId}_${System.currentTimeMillis()}.m4a"
        val tempPath = storageService.store(filePart, tempFilename)
        
        return try {
            val convertedFile = audioConversionService.convertM4aToWav(tempPath.toFile())
            val audioFile = AudioFile(
                userId = userId,
                phraseId = phraseId,
                filePath = convertedFile.absolutePath
            )
            audioFileRepository.save(audioFile).awaitSingle()
        } finally {
            tempPath.toFile().delete()
        }
    }

    suspend fun retrieveAudioStream(userId: Long, phraseId: Long, requestedFormat: String): Flux<DataBuffer> {
        validateUserAndPhrase(userId, phraseId)
        val audioFile = audioFileRepository.findByUserIdAndPhraseId(userId, phraseId)
            .awaitSingleOrNull() ?: throw AudioFileNotFoundException(
                path = "/audio/user/$userId/phrase/$phraseId",
                message = "Audio file not found for user $userId and phrase $phraseId"
            )

        val storedFile = storageService.retrieveStream(audioFile.filePath, requestedFormat, conversionDispatcher)

        // will only support m4a conversion for now
        return if (requestedFormat.equals("m4a", ignoreCase = true)) {
            val convertedFile = audioConversionService.convertWavToM4a(File(audioFile.filePath))
            storageService.retrieveStream(convertedFile.absolutePath, requestedFormat, conversionDispatcher)
        } else {
            storedFile
        }
    }
}
