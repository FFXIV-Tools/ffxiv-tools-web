package com.dkosub.ffxiv.tools.config

import javax.inject.Inject

class DatabaseConfig @Inject constructor(
    private val env: Environment
) {
    fun dialect(): String = env.variable("DATABASE_DIALECT")
    fun host(): String = env.variable("DATABASE_HOST")
    fun port(): Int = env.variable("DATABASE_PORT").toInt()
    fun databaseName(): String = env.variable("DATABASE_NAME")
    fun username(): String = env.variable("DATABASE_USERNAME")
    fun password(): String = env.variable("DATABASE_PASSWORD")
    fun redisUri(): String = env.variable("REDIS_URI")
}
