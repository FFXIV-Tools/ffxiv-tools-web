package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.model.request.CreateWatchRequest
import com.dkosub.ffxiv.tools.model.request.WatchType
import com.dkosub.ffxiv.tools.model.response.Watch
import com.dkosub.ffxiv.tools.service.WatchService
import io.jooby.Context
import io.jooby.annotation.*
import io.jooby.kt.body
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Path("/api/v1/watches")
class WatchController @Inject constructor(
    private val service: WatchService,
) {
    @GET
    suspend fun list(ctx: Context): List<Watch> {
        val account = ctx.validateAccount()
        return service.getAll(account)
    }

    @POST
    suspend fun create(ctx: Context): List<Watch> {
        val account = ctx.validateAccount()
        val body = ctx.body(CreateWatchRequest::class)

        // TODO: SQLDelight doesn't support RETURNING so we have to return the full list for now
        return when (body.type) {
            WatchType.ITEM -> service.createForItem(account, body.id)
            WatchType.RECIPE -> service.createForRecipe(account, body.id)
        }
    }

    @DELETE("/{id}")
    suspend fun delete(@PathParam id: Long, ctx: Context) {
        val account = ctx.validateAccount()
        service.delete(account, id)
    }
}
