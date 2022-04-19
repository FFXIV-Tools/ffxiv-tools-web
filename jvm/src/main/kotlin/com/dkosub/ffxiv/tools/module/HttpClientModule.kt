package com.dkosub.ffxiv.tools.module

import dagger.Module
import dagger.Provides
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import javax.inject.Singleton

@Module
class HttpClientModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                jackson()
            }
        }
    }
}
