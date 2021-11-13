package com.dkosub.ffxiv.tools.module

import com.dkosub.ffxiv.tools.config.DatabaseConfig
import com.dkosub.ffxiv.tools.config.JWTConfig
import com.dkosub.ffxiv.tools.config.OAuthConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ConfigurationModule {
    @Provides
    @Singleton
    fun databaseConfig() = DatabaseConfig()

    @Provides
    @Singleton
    fun provideJWTConfig() = JWTConfig()

    @Provides
    @Singleton
    fun provideOAuthConfig() = OAuthConfig()
}
