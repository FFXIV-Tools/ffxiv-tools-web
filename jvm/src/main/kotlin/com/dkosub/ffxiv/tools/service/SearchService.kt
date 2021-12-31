package com.dkosub.ffxiv.tools.service

import com.dkosub.ffxiv.tools.model.response.SearchResult
import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val FILTER_REGEX = Regex("^(([ir]):)?(.+)$")

@Singleton
class SearchService @Inject constructor(
    private val db: Database,
) {
    suspend fun execute(query: String): List<SearchResult> {
        val match = FILTER_REGEX.matchEntire(query) ?: throw RuntimeException("regex doesn't match")
        val (_, type, text) = match.destructured
        val searchQuery = "%${text}%"

        println("$type, $text")

        // TODO: This looks like complete trash
        return when (type) {
            "i" -> db.searchQueries.getItemResults(searchQuery)
                .asFlow()
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
            "r" -> db.searchQueries.getRecipeResults(searchQuery)
                .asFlow()
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
            else -> db.searchQueries.getAllResults(searchQuery)
                .asFlow()
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
}
