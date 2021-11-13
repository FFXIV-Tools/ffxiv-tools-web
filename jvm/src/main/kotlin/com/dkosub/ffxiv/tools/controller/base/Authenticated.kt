package com.dkosub.ffxiv.tools.controller.base

import com.dkosub.ffxiv.tools.model.Account
import com.dkosub.ffxiv.tools.service.AuthService
import io.jooby.Context
import io.jooby.StatusCode
import io.jooby.exception.StatusCodeException

private val BEARER_REGEX = Regex("^Bearer (.*)$")

open class Authenticated(
    private val authService: AuthService,
) {
    suspend fun validateUser(ctx: Context): Account {
        val authHeader = ctx.header("authorization")
        if (authHeader.isMissing) {
            throw StatusCodeException(StatusCode.UNAUTHORIZED)
        }

        val matchResult = BEARER_REGEX.find(authHeader.toString())
            ?: throw StatusCodeException(StatusCode.UNAUTHORIZED)

        val (token) = matchResult.destructured
        return authService.verifyAccount(token)
    }
}
