package com.dkosub.ffxiv.tools.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class DatabaseConfigTest {
    @Test
    fun whenLoaded_thenFetchesRelevantVariables() {
        val environment: Environment = mock()
        doReturn("dialect").whenever(environment).variable("DATABASE_DIALECT")
        doReturn("host").whenever(environment).variable("DATABASE_HOST")
        doReturn("12345").whenever(environment).variable("DATABASE_PORT")
        doReturn("name").whenever(environment).variable("DATABASE_NAME")
        doReturn("username").whenever(environment).variable("DATABASE_USERNAME")
        doReturn("password").whenever(environment).variable("DATABASE_PASSWORD")
        doReturn("redis").whenever(environment).variable("REDIS_URI")

        val config = DatabaseConfig(environment)
        assertEquals("dialect", config.dialect())
        assertEquals("host", config.host())
        assertEquals(12345, config.port())
        assertEquals("name", config.databaseName())
        assertEquals("username", config.username())
        assertEquals("password", config.password())
        assertEquals("redis", config.redisUri())
    }
}
