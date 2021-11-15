package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.controller.base.Authenticated
import com.dkosub.ffxiv.tools.enm.DeleteStatus
import com.dkosub.ffxiv.tools.model.request.CreateWatchRequest
import com.dkosub.ffxiv.tools.model.request.WatchType
import com.dkosub.ffxiv.tools.model.response.Watch
import com.dkosub.ffxiv.tools.service.AuthService
import com.dkosub.ffxiv.tools.service.WatchService
import io.jooby.Context
import io.jooby.StatusCode
import io.jooby.annotations.DELETE
import io.jooby.annotations.GET
import io.jooby.annotations.POST
import io.jooby.annotations.Path
import io.jooby.body
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Path("/api/v1/watches")
class WatchController @Inject constructor(
    authService: AuthService,
    private val service: WatchService,
) : Authenticated(authService) {
    @GET
    suspend fun list(ctx: Context): List<Watch> {
        val account = validateAccount(ctx)
        return service.getAll(account)
    }

    @POST
    suspend fun create(ctx: Context): List<Watch> {
        val account = validateAccount(ctx)
        val body = ctx.body<CreateWatchRequest>()

        // TODO: SQLDelight doesn't support RETURNING so we have to return the full list for now
        return when (body.type) {
            WatchType.ITEM -> service.createForItem(account, body.id)
            WatchType.RECIPE -> service.createForRecipe(account, body.id)
        }
    }

    @DELETE("/{id}")
    suspend fun delete(ctx: Context) {
        val account = validateAccount(ctx)
        val id = ctx.path("id").longValue()

        when (service.delete(account, id)) {
            DeleteStatus.DELETED -> ctx.responseCode = StatusCode.NO_CONTENT
            DeleteStatus.NOT_AUTHORIZED -> ctx.responseCode = StatusCode.FORBIDDEN
            DeleteStatus.NOT_FOUND -> ctx.responseCode = StatusCode.NOT_FOUND
        }
    }
}
