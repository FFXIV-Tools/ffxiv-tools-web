package com.dkosub.ffxiv.tools.service

import com.dkosub.ffxiv.tools.model.Account
import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import io.jooby.exception.UnauthorizedException
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val db: Database,
) {
    suspend fun getAccount(id: Long): Account {
        val account = db.accountQueries.get(id).asFlow()
            .mapToOneOrNull()
            .first()
            ?: throw UnauthorizedException("member not found")

        return Account(
            id = account.id,
            name = account.name,
            datacenterId = account.datacenter_id,
            worldId = account.world_id,
        )
    }

    fun loginAccount(id: Long) {
        db.accountQueries.upsert(id)
    }
}
