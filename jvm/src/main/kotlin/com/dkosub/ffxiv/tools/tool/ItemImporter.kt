package com.dkosub.ffxiv.tools.tool

import com.dkosub.ffxiv.tools.module.DatabaseModule
import com.dkosub.ffxiv.tools.module.HttpClientModule
import com.dkosub.ffxiv.tools.repository.Database
import com.dkosub.ffxiv.tools.util.parsing.EXDParser
import dagger.Component
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import javax.inject.Singleton

private const val ITEM_CSV_URL = "https://raw.githubusercontent.com/xivapi/ffxiv-datamining/master/csv/Item.csv"
private const val UNIVERSALIS_MARKETABLE_URL = "https://universalis.app/api/marketable"

@Singleton
@Component(modules = [DatabaseModule::class, HttpClientModule::class])
interface ItemImporterApplication {
    fun database(): Database

    fun httpClient(): HttpClient
}

private suspend fun fetchMarketableItems(client: HttpClient): List<Int> {
    return client.request(UNIVERSALIS_MARKETABLE_URL).body()
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

    EXDParser(response.body()).parse { row ->
        val id = (row["#"] as String).toInt()
        val name = row["Name"] as String
        val icon = (row["Icon"] as String).toInt()

        database.itemQueries.createItem(
            id = id,
            name = name,
            icon = icon,
            marketable = marketableItems.contains(id),
        )
    }
}
