package com.dkosub.ffxiv.tools.module

import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.sqlite.driver.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(): Database {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:postgresql://localhost:5432/ffxivtools"
        // TODO: Eventually integrate this with some sort of configuration :)
        config.username = "ffxivtools"
        config.password = "ffxivtools"

        val driver = HikariDataSource(config).asJdbcDriver()
        return Database(driver)
    }
}
