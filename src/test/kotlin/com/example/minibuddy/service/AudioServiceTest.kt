package com.example.minibuddy.service

import com.example.minibuddy.exception.InvalidUserOrPhraseException
import com.example.minibuddy.model.AudioFile
import com.example.minibuddy.repository.AudioFileRepository
import com.example.minibuddy.repository.UserRepository
import com.example.minibuddy.repository.PhraseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths
import com.example.minibuddy.config.StorageConfig

class AudioServiceTest {

    private lateinit var audioFileRepository: AudioFileRepository
    private lateinit var userRepository: UserRepository
    private lateinit var phraseRepository: PhraseRepository
    private lateinit var storageService: StorageService
    private lateinit var audioConversionService: AudioConversionService
    private lateinit var audioService: AudioService

    @BeforeEach
    fun setUp() {
        audioFileRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        phraseRepository = mockk(relaxed = true)
        storageService = mockk(relaxed = true)
        audioConversionService = mockk(relaxed = true)
        
        audioService = AudioService(
            audioFileRepository,
            storageService,
            audioConversionService,
            userRepository,
            phraseRepository,
        )

        // Default mock behavior for validation
        coEvery { userRepository.existsById(any<Long>()) } returns Mono.just(true)
        coEvery { phraseRepository.existsById(any<Long>()) } returns Mono.just(true)
    }

    @Test
    fun `storeAudio should throw InvalidUserOrPhraseException when user doesn't exist`(): Unit = runBlocking {
        // given
        coEvery { userRepository.existsById(1) } returns Mono.just(false)
        val dummyFilePart = DummyFilePart("test.m4a")

        // then
        assertThrows<InvalidUserOrPhraseException> {
            // when
            runBlocking { audioService.storeAudio(1, 1, dummyFilePart) }
        }
    }

    @Test
    fun `storeAudio should throw InvalidUserOrPhraseException when phrase doesn't exist`(): Unit = runBlocking {
        // given
        coEvery { phraseRepository.existsById(1) } returns Mono.just(false)
        val dummyFilePart = DummyFilePart("test.m4a")

        // then
        assertThrows<InvalidUserOrPhraseException> {
            // when
            runBlocking { audioService.storeAudio(1, 1, dummyFilePart) }
        }
    }

    @Test
    fun `storeAudio should return saved audio file when user and phrase exist`() = runBlocking {
        // given
        val dummyFilePart = DummyFilePart("test.m4a")
        val dummyPath = Paths.get("audio_storage/test.m4a")
        coEvery { storageService.store(dummyFilePart, any()) } returns dummyPath
        val dummyAudioFile = AudioFile(id = 1, userId = 1, phraseId = 1, filePath = dummyPath.toString())
        coEvery { audioFileRepository.save(any()) } returns Mono.just(dummyAudioFile)

        // when
        val result = audioService.storeAudio(1, 1, dummyFilePart)

        // then
        assert(result.id == 1L)
        coVerify { userRepository.existsById(1) }
        coVerify { phraseRepository.existsById(1) }
        coVerify { storageService.store(dummyFilePart, any()) }
        coVerify { audioFileRepository.save(any()) }
    }

    @Test
    fun `retrieveAudioStream should throw InvalidUserOrPhraseException when user doesn't exist`(): Unit = runBlocking {
        // given
        coEvery { userRepository.existsById(1) } returns Mono.just(false)

        // then
        assertThrows<InvalidUserOrPhraseException> {
            // when
            runBlocking { audioService.retrieveAudioStream(1, 1, "wav") }
        }
    }

    @Test
    fun `retrieveAudioStream should throw InvalidUserOrPhraseException when phrase doesn't exist`(): Unit = runBlocking {
        // given
        coEvery { phraseRepository.existsById(1) } returns Mono.just(false)

        // then
        assertThrows<InvalidUserOrPhraseException> {
            // when
            runBlocking { audioService.retrieveAudioStream(1, 1, "wav") }
        }
    }

    @Test
    fun `retrieveAudioStream should stream file content when exists`() = runBlocking {
        // given
        val testStorageConfig = StorageConfig().apply {
            basePath = "test_audio_storage"
        }
        val realStorageService = StorageService(testStorageConfig)
        val realConversionService = AudioConversionService()
        audioService = AudioService(
            audioFileRepository,
            realStorageService,
            realConversionService,
            userRepository,
            phraseRepository
        )
        val tempFile: File = File.createTempFile("test", ".wav").apply {
            writeText("dummy content", StandardCharsets.UTF_8)
            deleteOnExit()
        }
        val dummyAudioFile = AudioFile(id = 1, userId = 1, phraseId = 1, filePath = tempFile.absolutePath)
        coEvery { audioFileRepository.findByUserIdAndPhraseId(1, 1) } returns Mono.just(dummyAudioFile)

        // when
        val resultFlux: Flux<DataBuffer> = audioService.retrieveAudioStream(1, 1, "wav")
        val joinedBuffer = DataBufferUtils.join(resultFlux).block()
        val bytes = ByteArray(joinedBuffer!!.readableByteCount())
        joinedBuffer.read(bytes)
        val fileContent = String(bytes, StandardCharsets.UTF_8)

        // then
        assert(fileContent == "dummy content")
    }

    class DummyFilePart(private val filename: String) : FilePart {
        override fun filename(): String = filename
        override fun name(): String = "dummy"
        override fun headers(): HttpHeaders = HttpHeaders.EMPTY
        override fun transferTo(dest: Path): Mono<Void> = Mono.empty()
        override fun content(): Flux<DataBuffer> = Flux.empty()
    }
}
