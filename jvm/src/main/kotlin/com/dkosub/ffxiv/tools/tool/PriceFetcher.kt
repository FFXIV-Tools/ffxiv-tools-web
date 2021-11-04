package com.dkosub.ffxiv.tools.tool

import com.dkosub.ffxiv.tools.job.UniversalisJob
import com.dkosub.ffxiv.tools.module.ConfigurationModule
import com.dkosub.ffxiv.tools.module.DatabaseModule
import com.dkosub.ffxiv.tools.module.HttpClientModule
import com.dkosub.ffxiv.tools.repository.Database
import dagger.Component
import io.ktor.client.*
import javax.inject.Singleton

@Singleton
@Component(modules = [ConfigurationModule::class, DatabaseModule::class, HttpClientModule::class])
interface PriceFetcherApplication {
    fun database(): Database

    fun httpClient(): HttpClient
}

suspend fun main() {
    val dagger = DaggerPriceFetcherApplication.create()
    val job = UniversalisJob(dagger.httpClient(), dagger.database())

    job.hourlyFetch()
}
