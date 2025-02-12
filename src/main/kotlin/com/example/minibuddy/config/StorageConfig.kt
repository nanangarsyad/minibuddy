package com.example.minibuddy.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.nio.file.Path
import kotlin.io.path.Path

@Configuration
@ConfigurationProperties(prefix = "app.storage")
class StorageConfig {
    var basePath: String = "audio_storage"
    
    fun getStoragePath(): Path = Path(basePath)
}
