package com.example.minibuddy.config

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(FlywayProperties::class)
class DatabaseConfig {

    @Bean(initMethod = "migrate")
    fun flyway(properties: FlywayProperties) = Flyway.configure()
        .dataSource(
            properties.url,
            properties.user,
            properties.password
        )
        .locations(*properties.locations.toTypedArray())
        .baselineOnMigrate(properties.isBaselineOnMigrate)
        .validateOnMigrate(properties.isValidateOnMigrate)
        .load()
}
