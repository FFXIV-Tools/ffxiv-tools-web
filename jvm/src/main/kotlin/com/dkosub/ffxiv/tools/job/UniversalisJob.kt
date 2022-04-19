package com.dkosub.ffxiv.tools.job

import com.dkosub.ffxiv.tools.model.universalis.CurrentlyShownResponse
import com.dkosub.ffxiv.tools.model.universalis.HistoryResponse
import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.jooby.quartz.Scheduled
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

private const val UNIVERSALIS_CURRENTLY_SHOWN_URL = "https://universalis.app/api/Primal/"
private const val UNIVERSALIS_HISTORY_URL = "https://universalis.app/api/history/55/"

@Singleton
class UniversalisJob @Inject constructor(
    private val client: HttpClient,
    private val db: Database,
) {
    companion object {
        @JvmStatic
        val log: Logger = LoggerFactory.getLogger(UniversalisJob::class.java)
    }

    @Scheduled("1h")
    fun hourlyFetch() {
        log.info("Beginning hourly Universalis fetch")

        val priceFetchMs = measureTimeMillis { runBlocking { fetchPrices() } }
        log.info("Elapsed time fetching prices: {}ms", priceFetchMs)

        val velocityFetchMs = measureTimeMillis { runBlocking { fetchVelocities() } }
        log.info("Elapsed time fetching sale velocity: {}ms", velocityFetchMs)

        log.info("Hourly Universalis fetch complete")
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

    private suspend fun fetchPrices() {
        val allIds = db.itemQueries.listMarketableItems()
            .asFlow()
            .mapToList()
            .first()

        allIds.chunked(100).forEach { ids ->
            val url = UNIVERSALIS_CURRENTLY_SHOWN_URL + ids.joinToString(",") + "?entries=0&noGst=1"
            val response: HttpResponse = client.get(url)
            if (!response.status.isSuccess()) {
                log.warn("Failed to process item price batch {}", ids.joinToString())
                return
            }

            response.body<CurrentlyShownResponse>().items.forEach { item ->
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

                db.itemQueries.updateDatacenterPrices(
                    datacenterId = 5,
                    itemId = item.itemID,
                    minimum = datacenterMinimum,
                    mean = datacenterMean,
                    deviation = datacenterDeviation
                )
                for ((worldId, items) in itemsByWorld) {
                    val (worldMinimum, worldMean, worldDeviation) = calculateStats(items)
                    db.itemQueries.updateWorldPrices(
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

    private suspend fun fetchVelocities() {
        val allIds = db.itemQueries.listMarketableItems()
            .asFlow()
            .mapToList()
            .first()

        allIds.chunked(100).forEach { ids ->
            val url = UNIVERSALIS_HISTORY_URL + ids.joinToString(",") + "?entries=0&statsWithin=1209600000"
            val response: HttpResponse = client.get(url)
            if (!response.status.isSuccess()) {
                log.warn("Failed to process item velocity batch {}", ids.joinToString())
                return
            }

            response.body<HistoryResponse>().items.forEach { item ->
                db.itemQueries.updateWorldVelocity(
                    worldId = 55,
                    itemId = item.itemID,
                    nqVelocity = item.nqSaleVelocity,
                    hqVelocity = item.hqSaleVelocity,
                )
            }
            delay(100)
        }
    }
}
