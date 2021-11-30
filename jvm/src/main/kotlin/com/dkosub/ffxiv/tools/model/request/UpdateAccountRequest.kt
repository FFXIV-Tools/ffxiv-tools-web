package com.dkosub.ffxiv.tools.model.request

data class UpdateAccountRequest(
    val name: String,
    val datacenterId: Int,
    val worldId: Int,
)
