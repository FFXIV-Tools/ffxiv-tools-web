import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("kapt") version "1.9.0"
    application

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.squareup.sqldelight") version "1.5.5"
    id("org.flywaydb.flyway") version "9.20.0"
}

dependencies {
    // Jooby core
    implementation("io.jooby:jooby:3.0.0")
    implementation("io.jooby:jooby-kotlin:3.0.0")
    // Web server
    implementation("io.jooby:jooby-undertow:3.0.0")
    kapt("io.jooby:jooby-apt:3.0.0")
    // Jackson support
    implementation("io.jooby:jooby-jackson:3.0.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    // Session management
    implementation("io.jooby:jooby-redis:3.0.0")

    // Dependency injection
    implementation("com.google.dagger:dagger:2.47")
    kapt("com.google.dagger:dagger-compiler:2.47")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.8")

    // Scheduling
    implementation("io.jooby:jooby-quartz:3.0.0")

    // Database
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.squareup.sqldelight:coroutines-extensions:1.5.5")
    implementation("com.squareup.sqldelight:jdbc-driver:1.5.5")

    // HTTP client
    implementation("io.ktor:ktor-client-core:2.3.2")
    implementation("io.ktor:ktor-client-java:2.3.2")
    // JSON support
    implementation("io.ktor:ktor-client-content-negotiation:2.3.2")
    implementation("io.ktor:ktor-serialization-jackson:2.3.2")

    // CSV Parsing
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.1")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}

application {
    mainClass.set("com.dkosub.ffxiv.tools.MainKt")
}

flyway {
    val dbDialect = System.getenv("DATABASE_DIALECT") ?: "postgresql"
    val dbHost = System.getenv("DATABASE_HOST") ?: "localhost"
    val dbPort = System.getenv("DATABASE_PORT") ?: 5432
    val dbName = System.getenv("DATABASE_NAME") ?: "ffxivtools"

    url = "jdbc:$dbDialect://$dbHost:$dbPort/$dbName"
    user = System.getenv("DATABASE_USERNAME") ?: "postgres"
    password = System.getenv("DATABASE_USERNAME") ?: "postgres"
}

kapt {
    correctErrorTypes = true
}

sqldelight {
    database("Database") {
        packageName = "com.dkosub.ffxiv.tools.repository"
        dialect = System.getenv("DATABASE_DIALECT") ?: "postgresql"
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    archiveFileName.set("ffxiv-tools.jar")
    mergeServiceFiles()
}
