package com.dkosub.ffxiv.tools

import com.dkosub.ffxiv.tools.config.DatabaseConfig
import com.dkosub.ffxiv.tools.controller.AccountController
import com.dkosub.ffxiv.tools.controller.AuthController
import com.dkosub.ffxiv.tools.controller.SearchController
import com.dkosub.ffxiv.tools.controller.WatchController
import com.dkosub.ffxiv.tools.controller.market.CurrencyController
import com.dkosub.ffxiv.tools.job.UniversalisJob
import com.dkosub.ffxiv.tools.module.DatabaseModule
import com.dkosub.ffxiv.tools.module.HttpClientModule
import com.dkosub.ffxiv.tools.service.AuthService
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import dagger.Component
import io.jooby.Cookie
import io.jooby.RouterOption
import io.jooby.SessionToken
import io.jooby.handler.AccessLogHandler
import io.jooby.jackson.JacksonModule
import io.jooby.kt.require
import io.jooby.kt.runApp
import io.jooby.quartz.QuartzModule
import io.jooby.redis.RedisModule
import io.jooby.redis.RedisSessionStore
import io.lettuce.core.RedisClient
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@Singleton
@Component(modules = [DatabaseModule::class, HttpClientModule::class])
interface Application {
    // Config
    fun databaseConfig(): DatabaseConfig

    // Controllers
    fun accountController(): AccountController
    fun authController(): AuthController
    fun currencyController(): CurrencyController
    fun searchController(): SearchController
    fun watchController(): WatchController

    // Jobs
    fun universalisJob(): UniversalisJob

    // Services
    fun authService(): AuthService
}

fun main(args: Array<String>) {
    runApp(args) {
        val dagger = DaggerApplication.create()

        // Register services
        services.put(AuthService::class.java, dagger.authService())
        services.put(KotlinModule::class.java, kotlinModule())
        services.put(UniversalisJob::class.java, dagger.universalisJob())

        // Session management
        install(RedisModule(dagger.databaseConfig().redisUri()))

        sessionStore = RedisSessionStore(require<RedisClient>()).apply {
            timeout = 30.days.toJavaDuration()
            token = SessionToken.signedCookie(Cookie("session"))
        }

        // Configure JSON (de)serialization
        install(JacksonModule().module(KotlinModule::class.java))

        // Configure scheduled tasks
        install(QuartzModule(UniversalisJob::class.java))

        // Access logging
        use(AccessLogHandler())

        // Routing
        routerOptions(RouterOption.IGNORE_TRAILING_SLASH)
        coroutine {
            mvc(dagger.accountController())
            mvc(dagger.authController())
            mvc(dagger.currencyController())
            mvc(dagger.searchController())
            mvc(dagger.watchController())
        }
    }
}
