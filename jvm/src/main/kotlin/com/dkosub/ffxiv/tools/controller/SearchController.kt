package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.controller.base.Authenticated
import com.dkosub.ffxiv.tools.model.response.SearchResult
import com.dkosub.ffxiv.tools.repository.Database
import com.dkosub.ffxiv.tools.service.AuthService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.jooby.Context
import io.jooby.StatusCode
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.jooby.exception.StatusCodeException
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val LIKE_REPLACE_REGEX = Regex("([%_])")

@Singleton
@Path("/api/v1/search")
class SearchController @Inject constructor(
    authService: AuthService,
    private val db: Database,
) : Authenticated(authService) {
    @GET
    suspend fun search(ctx: Context): List<SearchResult> {
        val query = ctx.query("query")
        if (query.isMissing) {
            throw StatusCodeException(StatusCode.NOT_FOUND)
        }

        val escapedQuery = query.value()
            .replace(LIKE_REPLACE_REGEX, "\\\\\\\\$1")

        return db.searchQueries.getResults("%$escapedQuery%").asFlow()
            .mapToList()
            .first()
            .map {
                SearchResult(
                    id = it.id.toLong(),
                    type = it.type,
                    name = it.name
                )
            }
    }
}
