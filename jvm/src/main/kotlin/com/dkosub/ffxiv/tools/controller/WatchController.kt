package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.model.response.Watch
import com.dkosub.ffxiv.tools.service.WatchService
import io.jooby.Context
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Path("/api/v1/watches")
class WatchController @Inject constructor(
    private val service: WatchService
) {
    @GET
    suspend fun list(ctx: Context): List<Watch> {
        ctx.setResponseHeader("Access-Control-Allow-Origin", "*")
        return service.getAll()
    }
}
