package com.dkosub.ffxiv.tools.service

import com.dkosub.ffxiv.tools.model.Account
import com.dkosub.ffxiv.tools.repository.Database
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import io.jooby.StatusCode
import io.jooby.exception.StatusCodeException
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val jwtService: JWTService,
    private val db: Database,
) {
    fun loginAccount(id: Long): String {
        db.accountQueries.upsert(id)
        return jwtService.create(id)
    }

    suspend fun verifyAccount(token: String): Account {
        val id = jwtService.verify(token)
        val account = db.accountQueries.get(id).asFlow()
            .mapToOneOrNull()
            .first()
            ?: throw StatusCodeException(StatusCode.UNAUTHORIZED)

        return Account(
            id = account.id,
            name = account.name,
            datacenterId = account.datacenter_id,
            worldId = account.world_id,
        )
    }
}
