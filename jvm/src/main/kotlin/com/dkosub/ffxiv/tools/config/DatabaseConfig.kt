package com.dkosub.ffxiv.tools.config

class DatabaseConfig {
    fun dialect(): String = System.getenv("DATABASE_DIALECT")
    fun host(): String = System.getenv("DATABASE_HOST")
    fun port(): Int = System.getenv("DATABASE_PORT").toInt()
    fun databaseName(): String = System.getenv("DATABASE_NAME")
    fun username(): String = System.getenv("DATABASE_USERNAME")
    fun password(): String = System.getenv("DATABASE_PASSWORD")
    fun redisUri(): String = System.getenv("REDIS_URI")
}
