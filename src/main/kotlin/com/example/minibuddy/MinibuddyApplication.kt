package com.example.minibuddy

import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.availability.AvailabilityChangeEvent
import org.springframework.boot.availability.LivenessState
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import reactor.core.publisher.Mono
import java.time.Duration

@SpringBootApplication
class MinibuddyApplication(
    private val connectionFactory: ConnectionFactory,
    private val applicationContext: ApplicationContext
) {
    @EventListener(ContextRefreshedEvent::class)
    fun validateConnection() {
        try {
            Mono.from(connectionFactory.create())
                .timeout(Duration.ofSeconds(5))
                .block()
        } catch (e: Exception) {
            AvailabilityChangeEvent.publish(applicationContext, LivenessState.BROKEN)
            throw IllegalStateException("Failed to establish database connection. Shutting down.", e)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<MinibuddyApplication>(*args)
}
