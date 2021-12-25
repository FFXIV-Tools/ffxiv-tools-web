package com.dkosub.ffxiv.tools.service

import com.dkosub.ffxiv.tools.model.Account
import com.dkosub.ffxiv.tools.model.response.Material
import com.dkosub.ffxiv.tools.model.response.Watch
import com.dkosub.ffxiv.tools.repository.Database
import com.mchange.rmi.NotAuthorizedException
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import io.jooby.exception.NotFoundException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class WatchService @Inject constructor(
    private val db: Database
) {
    suspend fun createForItem(account: Account, id: Int): List<Watch> {
        db.watchQueries.createForItem(id, account.id)
        return getAll(account)
    }

    suspend fun createForRecipe(account: Account, id: Int): List<Watch> {
        db.watchQueries.createForRecipe(id, account.id)
        return getAll(account)
    }

    suspend fun delete(account: Account, id: Long) {
        val result = db.watchQueries.getOwner(id).asFlow()
            .mapToOneOrNull()
            .first()

        if (result == null) {
            throw NotFoundException("watch does not exist")
        } else if (account.id != result.account_id) {
            throw NotAuthorizedException("watch does not belong to account")
        }

        db.watchQueries.delete(id)
    }

    suspend fun getAll(account: Account) = getAll(account.id, account.datacenterId, account.worldId)

    private suspend fun getAll(accountId: Long, datacenterId: Int, worldId: Int): List<Watch> {
        val watchQuery = db.watchQueries.list(
            accountId = accountId,
            datacenterId = datacenterId,
            worldId = worldId
        )
        val watches = watchQuery.asFlow()
            .mapToList()
            .first()

        val allMaterials = if (watches.isNotEmpty()) {
            val listMaterialsQuery = db.watchQueries.listMaterials(
                watchIds = watches.map { it.id },
                datacenterId = datacenterId,
                worldId = worldId,
            )

            listMaterialsQuery.asFlow()
                .mapToList()
                .first()
        } else {
            emptyList()
        }

        return watches.map { watch ->
            val materials = allMaterials
                .filter { it.watch_id == watch.id }
                .map {
                    Material(
                        itemId = it.item_id,
                        name = it.name,
                        icon = it.icon,
                        quantity = it.quantity,
                        datacenterMinimum = it.datacenter_minimum,
                        datacenterMean = it.datacenter_mean,
                        datacenterDeviation = it.datacenter_deviation,
                        worldMinimum = it.world_minimum,
                        worldMean = it.world_mean,
                        worldDeviation = it.world_deviation
                    )
                }

            Watch(
                id = watch.id!!, // Not sure why SQLDelight views this as nullable?
                itemId = watch.item_id.toInt(),
                name = watch.name,
                icon = watch.icon,
                quantity = watch.quantity.toInt(),
                datacenterMinimum = watch.datacenter_minimum,
                datacenterMean = watch.datacenter_mean,
                datacenterDeviation = watch.datacenter_deviation,
                worldMinimum = watch.world_minimum,
                worldMean = watch.world_mean,
                worldDeviation = watch.world_deviation,
                materials = materials
            )
        }
    }
}
