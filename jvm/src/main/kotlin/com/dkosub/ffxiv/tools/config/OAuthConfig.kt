package com.dkosub.ffxiv.tools.config

class OAuthConfig {
    fun clientId(): String = System.getenv("OAUTH_CLIENT_ID")
    fun clientSecret(): String = System.getenv("OAUTH_CLIENT_SECRET")
    fun authorizeUrl(): String = System.getenv("OAUTH_AUTHORIZE_URL")
    fun redirectUrl(): String = System.getenv("OAUTH_REDIRECT_URL")
    fun tokenUrl(): String = System.getenv("OAUTH_TOKEN_URL")
}
