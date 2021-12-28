import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0-RC2"
    kotlin("kapt") version "1.6.0-RC2"
    application

    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("com.squareup.sqldelight") version "1.5.2"
    id("org.flywaydb.flyway") version "8.3.0"
}

dependencies {
    // Web server
    implementation("io.jooby:jooby-utow:2.11.0")
    kapt("io.jooby:jooby-apt:2.11.0")
    // Jackson support
    implementation("io.jooby:jooby-jackson:2.11.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    // Session management
    implementation("io.jooby:jooby-redis:2.11.0")

    // Dependency injection
    implementation("com.google.dagger:dagger:2.40.1")
    kapt("com.google.dagger:dagger-compiler:2.40.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.7")

    // Scheduling
    implementation("io.jooby:jooby-quartz:2.11.0")

    // Database
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("org.postgresql:postgresql:42.3.1")
    implementation("com.squareup.sqldelight:coroutines-extensions:1.5.2")
    implementation("com.squareup.sqldelight:jdbc-driver:1.5.2")

    // HTTP client
    implementation("io.ktor:ktor-client-core:1.6.4")
    implementation("io.ktor:ktor-client-java:1.6.4")
    implementation("io.ktor:ktor-client-jackson:1.6.4")

    // CSV Parsing
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")

    testImplementation(kotlin("test"))
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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "13"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("ffxiv-tools.jar")
    mergeServiceFiles()
}
