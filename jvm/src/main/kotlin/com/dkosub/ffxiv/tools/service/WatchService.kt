package com.dkosub.ffxiv.tools.service

import com.dkosub.ffxiv.tools.model.response.Material
import com.dkosub.ffxiv.tools.model.response.Watch
import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class WatchService @Inject constructor(
    private val db: Database
) {
    suspend fun getAll(): List<Watch> {
        // TODO: Need to eventually pass these in from the user session? Cater to Lamia for now.
        val watchers = db.watchQueries.list(5, 55).asFlow()
            .mapToList()
            .first()

        val listMaterialsQuery = db.watchQueries.listMaterials(
            watchIds = watchers.map { it.id },
            datacenterId = 5,
            worldId = 55,
        )

        val allMaterials = listMaterialsQuery.asFlow()
            .mapToList()
            .first()

        return watchers.map { watch ->
            val materials = allMaterials
                .filter { it.watch_id == watch.id }
                .map {
                    Material(
                        itemId = it.item_id,
                        name = it.name,
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
                itemId = watch.item_id.toInt(),
                name = watch.name,
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
