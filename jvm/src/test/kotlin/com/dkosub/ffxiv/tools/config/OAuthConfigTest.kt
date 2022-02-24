package com.dkosub.ffxiv.tools.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class OAuthConfigTest {
    @Test
    fun whenLoaded_thenFetchesRelevantVariables() {
        val environment: Environment = mock()
        doReturn("client-id").whenever(environment).variable("OAUTH_CLIENT_ID")
        doReturn("client-secret").whenever(environment).variable("OAUTH_CLIENT_SECRET")
        doReturn("authorize-url").whenever(environment).variable("OAUTH_AUTHORIZE_URL")
        doReturn("redirect-url").whenever(environment).variable("OAUTH_REDIRECT_URL")
        doReturn("token-url").whenever(environment).variable("OAUTH_TOKEN_URL")

        val config = OAuthConfig(environment)
        assertEquals("client-id", config.clientId())
        assertEquals("client-secret", config.clientSecret())
        assertEquals("authorize-url", config.authorizeUrl())
        assertEquals("redirect-url", config.redirectUrl())
        assertEquals("token-url", config.tokenUrl())
    }
}
