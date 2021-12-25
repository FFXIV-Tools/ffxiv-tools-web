package com.dkosub.ffxiv.tools.service.market

import com.dkosub.ffxiv.tools.model.Account
import com.dkosub.ffxiv.tools.model.response.Currency
import com.dkosub.ffxiv.tools.model.response.CurrencyItem
import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyService @Inject constructor(
    private val db: Database,
) {
    suspend fun getAll(): List<Currency> {
        return db.currencyQueries.list()
            .asFlow()
            .mapToList()
            .first()
            .map { Currency(id = it.id, name = it.name, icon = it.icon) }
    }

    suspend fun getItemsForCurrency(account: Account, id: Int): List<CurrencyItem> {
        val query = db.currencyQueries.listForCurrency(
            currencyId = id,
            datacenterId = account.datacenterId,
            worldId = account.worldId,
        )

        return query.asFlow()
            .mapToList()
            .first()
            .map {
                CurrencyItem(
                    id = it.id,
                    name = it.name,
                    icon = it.icon,
                    cost = it.cost,
                    quantity = it.quantity,
                    datacenterMinimum = it.datacenter_minimum,
                    datacenterMean = it.datacenter_mean,
                    datacenterDeviation = it.datacenter_deviation,
                    worldMinimum = it.world_minimum,
                    worldMean = it.world_mean,
                    worldDeviation = it.world_deviation,
                )
            }
    }
}
