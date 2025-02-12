package com.example.minibuddy.service

import com.example.minibuddy.config.StorageConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StorageServiceTest {

    private lateinit var storageService: StorageService
    private lateinit var storageConfig: StorageConfig
    private val testStorageDir = "test_audio_storage"
    private val storageDir = File(testStorageDir)

    @BeforeEach
    fun setUp() {
        if(storageDir.exists()) {
            storageDir.deleteRecursively()
        }
        storageConfig = StorageConfig().apply {
            basePath = testStorageDir
        }
        storageService = StorageService(storageConfig)
    }

    @AfterEach
    fun tearDown() {
        if(storageDir.exists()){
            storageDir.deleteRecursively()
        }
    }

    @Test
    fun `store should save file and return correct path`() = runBlocking {
        // given
        val sampleContent = "This is a test content."
        val dummyFilePart = DummyFilePart("sample.txt", sampleContent)
        val filename = "sample_stored.txt"
        
        // when
        val storedPath = storageService.store(dummyFilePart, filename)
        
        // then
        val storedFile = storedPath.toFile()
        assertTrue(storedFile.exists(), "Stored file should exist")
        val fileContent = storedFile.readText()
        assertEquals(sampleContent, fileContent)
    }

    class DummyFilePart(
        private val filename: String,
        private val content: String
    ) : FilePart {
        override fun filename(): String = filename
        override fun name(): String = "dummy"
        override fun headers(): HttpHeaders = HttpHeaders.EMPTY
        override fun content(): Flux<DataBuffer> {
            val factory = DefaultDataBufferFactory()
            val dataBuffer = factory.wrap(content.toByteArray())
            return Flux.just(dataBuffer)
        }
        override fun transferTo(dest: Path): Mono<Void> {
            return Mono.fromRunnable {
                dest.toFile().writeBytes(content.toByteArray())
            }
        }
    }
}
