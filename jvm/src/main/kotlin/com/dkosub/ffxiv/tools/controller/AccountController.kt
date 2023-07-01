package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.model.Account
import com.dkosub.ffxiv.tools.model.request.UpdateAccountRequest
import com.dkosub.ffxiv.tools.service.AccountService
import io.jooby.Context
import io.jooby.annotation.GET
import io.jooby.annotation.POST
import io.jooby.annotation.Path
import io.jooby.kt.body
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Path("/api/v1/accounts")
class AccountController @Inject constructor(
    val service: AccountService,
) {
    @GET("/@me")
    suspend fun getSessionAccount(ctx: Context) = ctx.validateAccount()

    @POST("/@me")
    suspend fun updateSessionAccount(ctx: Context): Account {
        val account = ctx.validateAccount()
        return service.updateAccount(account.id, ctx.body(UpdateAccountRequest::class))
    }
}
