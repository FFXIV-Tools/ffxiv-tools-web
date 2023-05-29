package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.model.response.SearchResult
import com.dkosub.ffxiv.tools.service.SearchService
import io.jooby.Context
import io.jooby.annotation.GET
import io.jooby.annotation.Path
import io.jooby.exception.BadRequestException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Path("/api/v1/search")
class SearchController @Inject constructor(
    private val service: SearchService,
) {
    @GET
    suspend fun search(ctx: Context): List<SearchResult> {
        ctx.validateAccount()

        val query = ctx.query("query")
        if (query.isMissing) {
            throw BadRequestException("missing 'query' parameter")
        }

        val escapedQuery = query.value()
            .trim()
            .replace(Regex("([%_])"), "\\\\\\\\$1")

        return service.execute(escapedQuery)
    }
}
