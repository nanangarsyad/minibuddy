package com.example.minibuddy.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.io.File
import org.slf4j.LoggerFactory

@Service
class AudioConversionService {
    private val logger = LoggerFactory.getLogger(AudioConversionService::class.java)

    suspend fun convertM4aToWav(inputFile: File): File = withContext(Dispatchers.IO) {
        logger.info("Converting M4A to WAV: ${inputFile.name}")
        val outputFile = File(inputFile.parent, inputFile.nameWithoutExtension + ".wav")
        val process = ProcessBuilder(
            "ffmpeg",
            "-i", inputFile.absolutePath,
            "-acodec", "pcm_s16le",
            "-ar", "44100",
            outputFile.absolutePath,
            "-y"
        ).redirectErrorStream(true).start()

        // Capture output for debugging
        val output = process.inputStream.bufferedReader().use { it.readText() }
        if (process.waitFor() != 0) {
            logger.error("FFmpeg conversion failed: $output")
            throw RuntimeException("FFmpeg conversion from m4a to wav failed: $output")
        }
        logger.info("Successfully converted to WAV: ${outputFile.name}")
        outputFile
    }

    suspend fun convertWavToM4a(inputFile: File): File = withContext(Dispatchers.IO) {
        logger.info("Converting WAV to M4A: ${inputFile.name}")
        val outputFile = File(inputFile.parent, inputFile.nameWithoutExtension + ".m4a")
        val process = ProcessBuilder(
            "ffmpeg",
            "-i", inputFile.absolutePath,
            "-acodec", "aac",
            "-b:a", "192k",
            outputFile.absolutePath,
            "-y"
        ).redirectErrorStream(true).start()

        // Capture output for debugging
        val output = process.inputStream.bufferedReader().use { it.readText() }
        if (process.waitFor() != 0) {
            logger.error("FFmpeg conversion failed: $output")
            throw RuntimeException("FFmpeg conversion from wav to m4a failed: $output")
        }
        logger.info("Successfully converted to M4A: ${outputFile.name}")
        outputFile
    }
}
