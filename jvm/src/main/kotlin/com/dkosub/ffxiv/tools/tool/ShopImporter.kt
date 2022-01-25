package com.dkosub.ffxiv.tools.tool

import com.dkosub.ffxiv.tools.module.ConfigurationModule
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

private const val SHOP_CSV_URL = "https://raw.githubusercontent.com/xivapi/ffxiv-datamining/master/csv/SpecialShop.csv"

private val CURRENCY_MAPPING = mapOf(
    2 to 25199, // White Crafters' Scrip
    4 to 25200, // White Gatherers' Scrip
    6 to 33913, // Purple Crafters' Scrip
    7 to 33914, // Purple Gatherers' Scrip
    26807 to 26807, // Bicolor Gemstone
    28063 to 28063, // Skybuilders' Scrip
    33870 to 33870, // Fête Token
)

@Singleton
@Component(modules = [ConfigurationModule::class, DatabaseModule::class, HttpClientModule::class])
private interface ShopImporterApplication {
    fun database(): Database

    fun httpClient(): HttpClient
}

suspend fun main() {
    val dagger = DaggerShopImporterApplication.create()
    val client = dagger.httpClient()
    val database = dagger.database()

    val response: HttpResponse = client.request(SHOP_CSV_URL)

    if (!response.status.isSuccess()) {
        println("Response from special shop CSV was not a success")
        return
    }

    database.currencyQueries.deleteAllItems()

    EXDParser(response.receive()).parse { row ->
        val count = row["Count"] as HashMap<String, *>
        val countCost = count["Cost"] as Array<Array<String>>
        val countReceive = count["Receive"] as Array<Array<String>>

        val item = row["Item"] as HashMap<String, *>
        val itemCost = item["Cost"] as Array<Array<String>>
        val itemReceive = item["Receive"] as Array<Array<String>>

        itemReceive[0].map { it.toInt() }
            .takeWhile { it > 0 }
            .forEachIndexed { index, itemId ->
                val currencyId = CURRENCY_MAPPING[itemCost[0][index].toInt()]
                val cost = countCost[0][index].toInt()
                val quantity = countReceive[0][index].toInt()

                if (currencyId == null) return@forEachIndexed

                database.currencyQueries.insertItem(
                    currencyId = currencyId,
                    itemId = itemId,
                    cost = cost,
                    quantity = quantity,
                )
            }
    }
}
