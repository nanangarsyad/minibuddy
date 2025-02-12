package com.example.minibuddy.service

import com.example.minibuddy.config.StorageConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.withContext
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@Service
class StorageService(storageConfig: StorageConfig) {

    private val storageDir: Path = storageConfig.getStoragePath().also {
        if (!it.toFile().exists()) it.toFile().mkdirs()
    }

    suspend fun store(filePart: FilePart, filename: String): Path {
        val destinationPath = storageDir.resolve(filename)
        return withContext(Dispatchers.IO) {
            AsynchronousFileChannel.open(
                destinationPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
            ).use { channel ->
                DataBufferUtils.write(filePart.content(), channel, 0)
                    .awaitLast()
                destinationPath
            }
        }
    }

    suspend fun retrieveStream(
        filePathStr: String, requestedFormat: String,
        conversionDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Flux<DataBuffer> = withContext(Dispatchers.IO) {
        val storedFile = File(filePathStr)
        if (!storedFile.exists()) {
            throw IllegalStateException("Stored file not found at: $filePathStr")
        }
        val fileToStream = if (storedFile.name.endsWith(requestedFormat)) {
            storedFile
        } else {
            withContext(conversionDispatcher) {
                val newFilePath = storedFile.absolutePath.replaceAfterLast(".", requestedFormat)
                storedFile.copyTo(File(newFilePath), overwrite = true)
                File(newFilePath)
            }
        }
        return@withContext DataBufferUtils.read(fileToStream.toPath(), DefaultDataBufferFactory(), 4096)
    }
}
