package com.dkosub.ffxiv.tools.tool

import com.dkosub.ffxiv.tools.module.DatabaseModule
import com.dkosub.ffxiv.tools.module.HttpClientModule
import com.dkosub.ffxiv.tools.repository.Database
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import dagger.Component
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.InputStream
import javax.inject.Singleton

private const val UNIVERSALIS_MARKETABLE_URL = "https://universalis.app/api/marketable"
private const val ITEM_CSV_URL = "https://raw.githubusercontent.com/xivapi/ffxiv-datamining/master/csv/Item.csv"

@Singleton
@Component(modules = [DatabaseModule::class, HttpClientModule::class])
interface ItemImporterApplication {
    fun database(): Database

    fun httpClient(): HttpClient
}

private suspend fun fetchMarketableItems(client: HttpClient): List<Int> {
    val response: HttpResponse = client.request(UNIVERSALIS_MARKETABLE_URL)
    return response.receive()
}

suspend fun main() {
    val dagger = DaggerItemImporterApplication.create()
    val client = dagger.httpClient()
    val database = dagger.database()

    val marketableItems = fetchMarketableItems(client)
    val response: HttpResponse = client.request(ITEM_CSV_URL)

    if (!response.status.isSuccess()) {
        println("Response from item CSV was not a success")
        return
    }

    csvReader().openAsync(response.receive<InputStream>()) {
        readNext() // Header 1
        readNext() // Header 2
        readNext() // Header 3
        readNext() // id = 0

        // Parse all items and insert into the item DB
        readAllAsSequence().forEach {
            val id = it[0].toInt()
            database.itemQueries.createItem(id, it[10], marketableItems.contains(id))
        }
    }
}
