import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    kotlin("kapt") version "1.6.20"
    application

    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.squareup.sqldelight") version "1.5.3"
    id("org.flywaydb.flyway") version "8.5.8"
}

dependencies {
    // Web server
    implementation("io.jooby:jooby-utow:2.13.0")
    kapt("io.jooby:jooby-apt:2.13.0")
    // Jackson support
    implementation("io.jooby:jooby-jackson:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    // Session management
    implementation("io.jooby:jooby-redis:2.13.0")

    // Dependency injection
    implementation("com.google.dagger:dagger:2.41")
    kapt("com.google.dagger:dagger-compiler:2.41")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.11")

    // Scheduling
    implementation("io.jooby:jooby-quartz:2.13.0")

    // Database
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.3.3")
    implementation("com.squareup.sqldelight:coroutines-extensions:1.5.3")
    implementation("com.squareup.sqldelight:jdbc-driver:1.5.3")

    // HTTP client
    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-java:2.0.0")
    // JSON support
    implementation("io.ktor:ktor-client-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-serialization-jackson:2.0.0")

    // CSV Parsing
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.mockito:mockito-inline:4.4.0")
}

application {
    mainClass.set("com.dkosub.ffxiv.tools.MainKt")
}

flyway {
    val dbDialect = System.getenv("DATABASE_DIALECT")
    val dbHost = System.getenv("DATABASE_HOST")
    val dbPort = System.getenv("DATABASE_PORT")
    val dbName = System.getenv("DATABASE_NAME")

    url = "jdbc:$dbDialect://$dbHost:$dbPort/$dbName"
    user = System.getenv("DATABASE_USERNAME")
    password = System.getenv("DATABASE_USERNAME")
}

sqldelight {
    database("Database") {
        packageName = "com.dkosub.ffxiv.tools.repository"
        dialect = "postgresql"
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    archiveFileName.set("ffxiv-tools.jar")
    mergeServiceFiles()
}
