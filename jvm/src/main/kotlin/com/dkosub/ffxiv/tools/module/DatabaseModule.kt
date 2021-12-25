package com.dkosub.ffxiv.tools.module

import com.dkosub.ffxiv.tools.config.DatabaseConfig
import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.sqlite.driver.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ConfigurationModule::class])
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(config: DatabaseConfig): Database {
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = "jdbc:${config.dialect()}://${config.host()}:${config.port()}/${config.databaseName()}"
        hikariConfig.username = config.username()
        hikariConfig.password = config.password()

        val driver = HikariDataSource(hikariConfig).asJdbcDriver()
        return Database(driver)
    }
}
