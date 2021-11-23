package com.dkosub.ffxiv.tools.controller

import io.jooby.Context
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Path("/api/v1/accounts")
class AccountController @Inject constructor() {
    @GET("/@me")
    suspend fun getSessionAccount(ctx: Context) = ctx.validateAccount()
}
