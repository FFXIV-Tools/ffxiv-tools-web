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

private const val RECIPE_CSV_URL = "https://raw.githubusercontent.com/xivapi/ffxiv-datamining/master/csv/Recipe.csv"

@Singleton
@Component(modules = [DatabaseModule::class, HttpClientModule::class])
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

    database.itemQueries.deleteMaterials()

    EXDParser(response.body()).parse { row ->
        val id = (row["#"] as String).toInt()

        val amount = row["Amount"] as HashMap<String, *>
        val amountIngredient = amount["Ingredient"] as Array<String>
        val amountResult = (amount["Result"] as String).toInt()

        val item = row["Item"] as HashMap<String, *>
        val itemIngredient = item["Ingredient"] as Array<String>
        val itemResult = (item["Result"] as String).toInt()

        if (itemResult <= 0) return@parse

        database.itemQueries.createRecipe(
            id = id,
            itemId = itemResult,
            quantity = amountResult,
        )

        itemIngredient.map { it.toInt() }
            .filter { it > 0 }
            .forEachIndexed { index, materialItemId ->
                database.itemQueries.addRecipeMaterial(
                    recipeId = id,
                    itemId = materialItemId,
                    quantity = amountIngredient[index].toInt()
                )
            }
    }
}
