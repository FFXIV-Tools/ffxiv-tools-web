package com.dkosub.ffxiv.tools.tool

import com.dkosub.ffxiv.tools.model.universalis.CurrentlyShownResponse
import com.dkosub.ffxiv.tools.module.DatabaseModule
import com.dkosub.ffxiv.tools.module.HttpClientModule
import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import dagger.Component
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Singleton
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

private const val UNIVERSALIS_HISTORY_URL = "https://universalis.app/api/Primal/"

@Singleton
@Component(modules = [DatabaseModule::class, HttpClientModule::class])
interface PriceFetcherApplication {
    fun database(): Database

    fun httpClient(): HttpClient
}

private fun calculateStats(items: List<Int>): Triple<Int, Int, Double> {
    val firstHalfItems = items.subList(0, ceil(items.size / 2.0).toInt())
    if (firstHalfItems.isEmpty()) {
        return Triple(0, 0, 0.0)
    }

    val minimum = firstHalfItems.minOf { it }
    val mean = firstHalfItems.sum() / firstHalfItems.size
    val deviation = sqrt(firstHalfItems.sumOf { (it - mean).toDouble().pow(2) } / firstHalfItems.size)

    return Triple(minimum, mean, deviation)
}

suspend fun fetchPrices() {
    val dagger = DaggerPriceFetcherApplication.create()
    val client = dagger.httpClient()
    val database = dagger.database()

    val allIds = database.itemQueries.listRecipeItems()
        .asFlow()
        .mapToList()
        .first()

    allIds.chunked(100).forEach { ids ->
        val url = UNIVERSALIS_HISTORY_URL + ids.joinToString(",") + "?entries=0&noGst=1"
        val response: HttpResponse = client.get(url) {
            expectSuccess = false
        }
        if (!response.status.isSuccess()) {
            println("Failed to process ${ids.joinToString()}")
            return
        }

        val currentlyShownResponse = response.receive<CurrentlyShownResponse>()
        for (item in currentlyShownResponse.items) {
            // TODO: This feels like a bad idea right now but I can't think of anything better.
            val allItems = item.listings.flatMap { listing ->
                MutableList(listing.quantity) { listing.pricePerUnit }
            }
            val itemsByWorld: Map<Int, List<Int>> = item.listings.groupBy { it.worldID }
                .mapValues {
                    it.value.flatMap { listing ->
                        MutableList(listing.quantity) { listing.pricePerUnit }
                    }
                }

            val (datacenterMinimum, datacenterMean, datacenterDeviation) = calculateStats(allItems)

            database.itemQueries.updateDatacenterPrices(
                datacenterId = 5,
                itemId = item.itemID,
                minimum = datacenterMinimum,
                mean = datacenterMean,
                deviation = datacenterDeviation
            )
            for ((worldId, items) in itemsByWorld) {
                val (worldMinimum, worldMean, worldDeviation) = calculateStats(items)
                database.itemQueries.updateWorldPrices(
                    worldId = worldId,
                    itemId = item.itemID,
                    minimum = worldMinimum,
                    mean = worldMean,
                    deviation = worldDeviation
                )
            }
        }
        delay(100)
    }
}

suspend fun main() {
    val elapsed = measureTimeMillis {
        fetchPrices()
    }
    println("Elapsed time: ${elapsed}ms")
}
