package com.dkosub.ffxiv.tools

import com.dkosub.ffxiv.tools.controller.WatchController
import com.dkosub.ffxiv.tools.job.UniversalisJob
import com.dkosub.ffxiv.tools.module.ConfigurationModule
import com.dkosub.ffxiv.tools.module.DatabaseModule
import com.dkosub.ffxiv.tools.module.HttpClientModule
import dagger.Component
import io.jooby.json.JacksonModule
import io.jooby.quartz.QuartzModule
import io.jooby.runApp
import javax.inject.Singleton

@Singleton
@Component(modules = [ConfigurationModule::class, DatabaseModule::class, HttpClientModule::class])
interface Application {
    // Controllers
    fun watchController(): WatchController

    // Jobs
    fun universalisJob(): UniversalisJob
}

fun main(args: Array<String>) {
    runApp(args) {
        val dagger = DaggerApplication.create()

        // Configure server modules
        install(JacksonModule())

        // Configure scheduled tasks
        services.put(UniversalisJob::class.java, dagger.universalisJob())
        install(QuartzModule(UniversalisJob::class.java))

        coroutine {
            mvc(dagger.watchController())
        }
    }
}
