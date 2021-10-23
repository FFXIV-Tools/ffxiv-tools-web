package com.dkosub.ffxiv.tools

import com.dkosub.ffxiv.tools.controller.WatchController
import com.dkosub.ffxiv.tools.module.DatabaseModule
import dagger.Component
import io.jooby.json.JacksonModule
import io.jooby.runApp
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class])
interface Application {
    fun watchController(): WatchController
}

fun main(args: Array<String>) {
    runApp(args) {
        val dagger = DaggerApplication.create()
        install(JacksonModule())

        coroutine {
            mvc(dagger.watchController())
        }
    }
}
