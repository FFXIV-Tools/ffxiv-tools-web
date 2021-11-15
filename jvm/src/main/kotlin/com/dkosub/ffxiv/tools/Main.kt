package com.dkosub.ffxiv.tools

import com.dkosub.ffxiv.tools.controller.AuthController
import com.dkosub.ffxiv.tools.controller.SearchController
import com.dkosub.ffxiv.tools.controller.WatchController
import com.dkosub.ffxiv.tools.job.UniversalisJob
import com.dkosub.ffxiv.tools.module.ConfigurationModule
import com.dkosub.ffxiv.tools.module.DatabaseModule
import com.dkosub.ffxiv.tools.module.HttpClientModule
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dagger.Component
import io.jooby.json.JacksonModule
import io.jooby.quartz.QuartzModule
import io.jooby.require
import io.jooby.runApp
import javax.inject.Singleton

@Singleton
@Component(modules = [ConfigurationModule::class, DatabaseModule::class, HttpClientModule::class])
interface Application {
    // Controllers
    fun authController(): AuthController
    fun searchController(): SearchController
    fun watchController(): WatchController

    // Jobs
    fun universalisJob(): UniversalisJob
}

fun main(args: Array<String>) {
    runApp(args) {
        val dagger = DaggerApplication.create()

        // Configure JSON (de)serialization
        install(JacksonModule())
        require<ObjectMapper>().registerKotlinModule()

        // Configure scheduled tasks
        services.put(UniversalisJob::class.java, dagger.universalisJob())
        install(QuartzModule(UniversalisJob::class.java))

        coroutine {
            mvc(dagger.authController())
            mvc(dagger.searchController())
            mvc(dagger.watchController())
        }
    }
}
