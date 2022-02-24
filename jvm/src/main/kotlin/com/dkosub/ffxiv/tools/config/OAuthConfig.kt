package com.dkosub.ffxiv.tools.config

import javax.inject.Inject

class OAuthConfig @Inject constructor(
    private val env: Environment
) {
    fun clientId(): String = env.variable("OAUTH_CLIENT_ID")
    fun clientSecret(): String = env.variable("OAUTH_CLIENT_SECRET")
    fun authorizeUrl(): String = env.variable("OAUTH_AUTHORIZE_URL")
    fun redirectUrl(): String = env.variable("OAUTH_REDIRECT_URL")
    fun tokenUrl(): String = env.variable("OAUTH_TOKEN_URL")
}
