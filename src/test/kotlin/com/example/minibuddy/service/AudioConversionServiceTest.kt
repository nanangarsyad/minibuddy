package com.example.minibuddy.service

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import java.io.File

class AudioConversionServiceTest {

    private val conversionService = AudioConversionService()

    @Test
    fun `toStorageFormat converts m4a to wav`(): Unit = runBlocking {
        // given 
        val resource = ClassPathResource("sample.m4a")
        val tempInput = File.createTempFile("test_sample", ".m4a").apply {
            resource.inputStream.copyTo(this.outputStream())
            deleteOnExit()
        }

        // when 
        val outputFile = conversionService.convertM4aToWav(tempInput)

        // then 
        assertTrue(outputFile.exists())
        assertTrue(outputFile.name.endsWith(".wav"))
        outputFile.delete()
    }

    @Test
    fun `toRetrievalFormat converts wav to m4a`(): Unit = runBlocking {
        // given
        val resource = ClassPathResource("sample.wav")
        val tempInput = File.createTempFile("test_sample", ".wav").apply {
            resource.inputStream.copyTo(this.outputStream())
            deleteOnExit()
        }

        // when 
        val outputFile = conversionService.convertWavToM4a(tempInput)
        
        // then 
        assertTrue(outputFile.exists())
        assertTrue(outputFile.name.endsWith(".m4a"))
        outputFile.delete()
    }
}
