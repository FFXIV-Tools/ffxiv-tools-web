package com.dkosub.ffxiv.tools.service

import com.dkosub.ffxiv.tools.model.Account
import com.dkosub.ffxiv.tools.model.asModel
import com.dkosub.ffxiv.tools.model.request.UpdateAccountRequest
import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountService @Inject constructor(
    val db: Database,
) {
    suspend fun updateAccount(id: Long, request: UpdateAccountRequest): Account {
        db.accountQueries.update(
            id = id,
            name = request.name,
            datacenterId = request.datacenterId,
            worldId = request.worldId,
        )

        return db.accountQueries.get(id)
            .asFlow()
            .mapToOne()
            .first()
            .asModel()
    }
}
