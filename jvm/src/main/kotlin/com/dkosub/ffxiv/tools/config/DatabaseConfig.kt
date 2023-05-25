package com.dkosub.ffxiv.tools.config

import javax.inject.Inject

class DatabaseConfig @Inject constructor(
    private val env: Environment
) {
    fun dialect(): String = env.variable("DATABASE_DIALECT", "postgresql")
    fun host(): String = env.variable("DATABASE_HOST", "localhost")
    fun port(): Int = env.variable("DATABASE_PORT", "5432").toInt()
    fun databaseName(): String = env.variable("DATABASE_NAME", "ffxivtools")
    fun username(): String = env.variable("DATABASE_USERNAME", "postgres")
    fun password(): String = env.variable("DATABASE_PASSWORD", "postgres")
    fun redisUri(): String = env.variable("REDIS_URI", "redis://localhost")
}
