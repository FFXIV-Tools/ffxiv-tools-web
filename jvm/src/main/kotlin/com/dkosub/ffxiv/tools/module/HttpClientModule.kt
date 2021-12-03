package com.dkosub.ffxiv.tools.module

import dagger.Module
import dagger.Provides
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import javax.inject.Singleton

@Module
class HttpClientModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Java) {
            install(JsonFeature)

            defaultRequest {
                // TODO: Import version in here at some point?
                header("User-Agent", "FFXIV Tools")
            }
        }
    }
}
