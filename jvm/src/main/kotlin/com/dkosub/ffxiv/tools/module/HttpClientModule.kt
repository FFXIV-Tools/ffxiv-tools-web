package com.dkosub.ffxiv.tools.module

import dagger.Module
import dagger.Provides
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.json.*
import javax.inject.Singleton

@Module
class HttpClientModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Java) {
            install(JsonFeature)
        }
    }
}
