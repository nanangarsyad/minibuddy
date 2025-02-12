package com.example.minibuddy.exception

import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AudioFileNotFoundException::class)
    fun handleAudioFileNotFound(ex: AudioFileNotFoundException): ProblemDetail {
        logger.warn("Audio file not found: ${ex.path}", ex)
        return ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
            title = "Audio File Not Found"
            detail = ex.message ?: "The requested audio file does not exist."
            setProperty("timestamp", LocalDateTime.now())
            setProperty("path", ex.path)
        }
    }

    @ExceptionHandler(InvalidAudioFileException::class)
    fun handleInvalidAudioFile(ex: InvalidAudioFileException): ProblemDetail {
        logger.warn("Invalid audio file: ${ex.path}", ex)
        return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            title = "Invalid Audio File"
            detail = ex.message ?: "The provided audio file is not valid."
            setProperty("timestamp", LocalDateTime.now())
            setProperty("path", ex.path)
        }
    }

    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKeyException(ex: DuplicateKeyException): ProblemDetail {
        logger.warn("Duplicate entry detected", ex)
        return ProblemDetail.forStatus(HttpStatus.CONFLICT).apply {
            title = "Conflict"
            detail = "A duplicate entry was detected. Please ensure the audio file for the given user and phrase is unique."
            setProperty("timestamp", LocalDateTime.now())
        }
    }

    @ExceptionHandler(InvalidUserOrPhraseException::class)
    fun handleInvalidUserOrPhrase(ex: InvalidUserOrPhraseException): ProblemDetail {
        logger.warn("Invalid user or phrase ID: ${ex.message}")
        return ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
            title = "Invalid User or Phrase ID"
            detail = ex.message
            setProperty("timestamp", LocalDateTime.now())
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ProblemDetail {
        logger.error("Unhandled exception", ex)
        return ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR).apply {
            title = "Internal Server Error"
            detail = ex.message ?: "An unexpected error occurred."
            setProperty("timestamp", LocalDateTime.now())
        }
    }
}

class AudioFileNotFoundException(val path: String, message: String) : RuntimeException(message)
class InvalidAudioFileException(val path: String, message: String) : RuntimeException(message)
class InvalidUserOrPhraseException(message: String) : RuntimeException(message)
