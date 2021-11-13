package com.dkosub.ffxiv.tools.config

class JWTConfig {
    fun issuer(): String = System.getenv("JWT_ISSUER")
    fun secret(): String = System.getenv("JWT_SECRET")
}
