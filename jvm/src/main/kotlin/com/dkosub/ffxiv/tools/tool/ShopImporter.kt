package com.dkosub.ffxiv.tools.tool

import com.dkosub.ffxiv.tools.module.ConfigurationModule
import com.dkosub.ffxiv.tools.module.DatabaseModule
import com.dkosub.ffxiv.tools.module.HttpClientModule
import com.dkosub.ffxiv.tools.repository.Database
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import dagger.Component
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.InputStream
import javax.inject.Singleton
import kotlin.math.max

private const val SHOP_CSV_URL = "https://raw.githubusercontent.com/xivapi/ffxiv-datamining/master/csv/SpecialShop.csv"

private val HEADER_REGEX = Regex("^([^{\\[]+)(\\{(\\w+)})?(\\[(\\d+)])?(\\[(\\d+)])?$")

private data class Column(
    val key: DataKey,
    val metadata: ColumnMetadata,
    val index1: Int,
    val index2: Int,
) {
    fun assign(r: HashMap<String, Any>, v: String) {
        var row = r
        var rowKey = key.key

        if (key.subKey != null) {
            row = row.getOrPut(key.key) { hashMapOf<String, Any>() } as HashMap<String, Any>
            rowKey = key.subKey
        }

        if (index1 >= 0) {
            if (index2 >= 0) {
                val array =
                    row.getOrPut(rowKey) { Array(metadata.index2Size) { Array<String?>(metadata.index1Size) { null } } } as Array<Array<String?>>
                array[index2][index1] = v
            } else {
                val array = row.getOrPut(rowKey) { Array<String?>(metadata.index1Size) { null } } as Array<String?>
                array[index1] = v
            }
        } else {
            row[rowKey] = v
        }
    }
}

private data class ColumnMetadata(
    var index1Size: Int = 0,
    var index2Size: Int = 0,
)

data class DataKey(
    val key: String,
    val subKey: String?,
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

    csvReader().openAsync(response.receive<InputStream>()) {
        // First line only contains the indexes, pretty useless
        readNext()

        val columnMetadataLookup = hashMapOf<DataKey, ColumnMetadata>()
        val columnMetadata = (readNext() ?: throw RuntimeException("Missing header row")).map {
            if (it.isEmpty()) return@map null

            val match = HEADER_REGEX.matchEntire(it) ?: throw RuntimeException("Header does not parse: $it")
            val key = match.groups[1]?.value ?: throw RuntimeException("No primary key")
            val subKey = match.groups[3]?.value
            val index1 = match.groups[5]?.value
            val index2 = match.groups[7]?.value
            val dataKey = DataKey(key, subKey)

            val metadata = columnMetadataLookup.getOrPut(dataKey) { ColumnMetadata() }
            if (index1 != null) metadata.index1Size = max(metadata.index1Size, index1.toInt() + 1)
            if (index2 != null) metadata.index2Size = max(metadata.index2Size, index2.toInt() + 1)

            return@map Column(dataKey, metadata, index1?.toInt() ?: -1, index2?.toInt() ?: -1)
        }

        // Skip the type and empty row
        repeat(2) { readNext() }

        val currencyMapping = mapOf(
            2 to 25199, // White Crafters' Scrip
            4 to 25200, // White Gatherers' Scrip
            6 to 33913, // Purple Crafters' Scrip
            7 to 33914, // Purple Gatherers' Scrip
            26807 to 26807, // Bicolor Gemstone
        )

        readAllAsSequence().forEach { row ->
            val rowData = hashMapOf<String, Any>()
            row.forEachIndexed { index, value ->
                columnMetadata[index]?.assign(rowData, value)
            }

            val count = rowData["Count"] as HashMap<String, *>
            val countCost = count["Cost"] as Array<Array<String>>
            val countReceive = count["Receive"] as Array<Array<String>>

            val item = rowData["Item"] as HashMap<String, *>
            val itemCost = item["Cost"] as Array<Array<String>>
            val itemReceive = item["Receive"] as Array<Array<String>>

            itemReceive[0].map { it.toInt() }
                .takeWhile { it > 0 }
                .forEachIndexed { index, itemId ->
                    val currencyId = currencyMapping[itemCost[0][index].toInt()]
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
}
