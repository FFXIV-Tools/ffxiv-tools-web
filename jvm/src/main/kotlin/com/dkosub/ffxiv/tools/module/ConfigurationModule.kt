package com.dkosub.ffxiv.tools.module

import com.dkosub.ffxiv.tools.config.DatabaseConfig
import com.dkosub.ffxiv.tools.config.Environment
import com.dkosub.ffxiv.tools.config.OAuthConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ConfigurationModule {
    @Provides
    @Singleton
    fun environment() = Environment()

    @Provides
    @Singleton
    fun databaseConfig(env: Environment) = DatabaseConfig(env)

    @Provides
    @Singleton
    fun provideOAuthConfig(env: Environment) = OAuthConfig(env)
}
