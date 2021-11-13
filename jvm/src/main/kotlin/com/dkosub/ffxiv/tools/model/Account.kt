package com.dkosub.ffxiv.tools.model

data class Account(
    val id: Long,
    val name: String,
    val datacenterId: Int,
    val worldId: Int,
)
