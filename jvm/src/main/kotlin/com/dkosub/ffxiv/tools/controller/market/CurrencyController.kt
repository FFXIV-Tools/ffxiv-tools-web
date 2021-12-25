package com.dkosub.ffxiv.tools.controller.market

import com.dkosub.ffxiv.tools.controller.validateAccount
import com.dkosub.ffxiv.tools.service.market.CurrencyService
import io.jooby.Context
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.jooby.annotations.PathParam
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Path("/api/v1/market/currencies")
class CurrencyController @Inject constructor(
    private val service: CurrencyService,
) {
    @GET
    suspend fun list() = service.getAll()

    @GET("/{id}")
    suspend fun listForCurrency(ctx: Context, @PathParam id: Int) =
        service.getItemsForCurrency(ctx.validateAccount(), id)
}
