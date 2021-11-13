package com.dkosub.ffxiv.tools.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.dkosub.ffxiv.tools.config.JWTConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JWTService @Inject constructor(
    config: JWTConfig,
) {
    private val algorithm = Algorithm.HMAC256(config.secret())
    private val issuer = config.issuer()

    private val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()

    fun create(accountId: Long): String = JWT.create()
        .withIssuer(issuer)
        .withJWTId(accountId.toString())
        .sign(algorithm)

    fun verify(token: String): Long = verifier.verify(token).id.toLong()
}
