package com.dkosub.ffxiv.tools.model.request

enum class WatchType {
    ITEM,
    RECIPE,
}

data class CreateWatchRequest(
    val id: Int,
    val type: WatchType,
)
