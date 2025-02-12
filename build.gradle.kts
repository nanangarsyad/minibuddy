plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.postgresql:r2dbc-postgresql:1.0.4.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.mockk:mockk:1.13.16")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.r2dbc:r2dbc-h2:1.0.0.RELEASE")
    testImplementation("io.rest-assured:kotlin-extensions:5.3.2")
    testImplementation("io.rest-assured:rest-assured:5.3.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
