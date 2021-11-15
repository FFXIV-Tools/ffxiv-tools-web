package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.model.Account
import com.dkosub.ffxiv.tools.service.AuthService
import io.jooby.Context
import io.jooby.exception.UnauthorizedException
import io.jooby.require

private val BEARER_REGEX = Regex("^Bearer (.*)$")

suspend fun Context.validateAccount(): Account {
    val authHeader = this.header().get("Authorization")
    if (authHeader.isMissing) {
        throw UnauthorizedException("missing 'Authorization' header")
    }

    val matchResult = BEARER_REGEX.find(authHeader.toString())
        ?: throw UnauthorizedException("invalid bearer")
    val (token) = matchResult.destructured

    return this.require<AuthService>().verifyAccount(token)
}
