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

private const val RECIPE_CSV_URL = "https://raw.githubusercontent.com/xivapi/ffxiv-datamining/master/csv/Recipe.csv"

@Singleton
@Component(modules = [ConfigurationModule::class, DatabaseModule::class, HttpClientModule::class])
interface RecipeImporterApplication {
    fun database(): Database

    fun httpClient(): HttpClient
}

suspend fun main() {
    val dagger = DaggerRecipeImporterApplication.create()
    val client = dagger.httpClient()
    val database = dagger.database()

    val response: HttpResponse = client.request(RECIPE_CSV_URL)

    if (!response.status.isSuccess()) {
        println("Response from recipe CSV was not a success")
        return
    }

    csvReader().openAsync(response.receive<InputStream>()) {
        // First 3 lines are headers, 4th is 0.
        repeat(4) { readNext() }

        // Parse all items and insert into the item DB
        readAllAsSequence().forEach eachRecipe@{ row ->
            val id = row[0].toInt()
            val itemId = row[4].toInt()
            if (itemId <= 0) return@eachRecipe

            database.itemQueries.createRecipe(id, itemId, row[5].toInt())
            row.slice(6..25).chunked(2).forEach eachMaterial@{
                val materialItemId = it[0].toInt()
                if (materialItemId <= 0) return@eachMaterial

                database.itemQueries.addRecipeMaterial(id, materialItemId, it[1].toInt())
            }
        }
    }
}
