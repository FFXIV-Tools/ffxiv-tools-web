package com.dkosub.ffxiv.tools.model

data class Account(
    val id: Long,
    val name: String,
    val datacenterId: Int,
    val worldId: Int,
)

fun com.dkosub.ffxiv.tools.repository.Account.asModel(): Account {
    return Account(
        id = this.id,
        name = this.name,
        datacenterId = this.datacenter_id,
        worldId = this.world_id,
    )
}
