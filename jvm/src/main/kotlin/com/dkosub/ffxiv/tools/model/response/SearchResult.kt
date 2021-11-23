package com.dkosub.ffxiv.tools.model.response

data class SearchResult(
    val id: Long,
    val type: String,
    val icon: Int,
    val name: String,
)
