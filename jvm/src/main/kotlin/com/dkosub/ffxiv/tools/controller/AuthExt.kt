package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.model.Account
import com.dkosub.ffxiv.tools.service.AuthService
import io.jooby.Context
import io.jooby.exception.UnauthorizedException
import io.jooby.require

suspend fun Context.validateAccount(): Account {
    val session = this.sessionOrNull()
        ?: throw UnauthorizedException("session not found")

    val id = session.get("id")
    if (id.isMissing) {
        throw UnauthorizedException("id missing from session")
    }

    return this.require<AuthService>().getAccount(id.longValue())
}
