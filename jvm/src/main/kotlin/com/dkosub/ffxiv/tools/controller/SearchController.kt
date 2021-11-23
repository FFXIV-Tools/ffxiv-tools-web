package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.model.response.SearchResult
import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.jooby.Context
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.jooby.exception.BadRequestException
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Path("/api/v1/search")
class SearchController @Inject constructor(
    private val db: Database,
) {
    @GET
    suspend fun search(ctx: Context): List<SearchResult> {
        ctx.validateAccount()

        val query = ctx.query("query")
        if (query.isMissing) {
            throw BadRequestException("missing 'query' parameter")
        }

        val escapedQuery = query.value()
            .replace(Regex("([%_])"), "\\\\\\\\$1")

        return db.searchQueries.getResults("%$escapedQuery%").asFlow()
            .mapToList()
            .first()
            .map {
                SearchResult(
                    id = it.id.toLong(),
                    type = it.type,
                    icon = it.icon,
                    name = it.name,
                )
            }
    }
}
