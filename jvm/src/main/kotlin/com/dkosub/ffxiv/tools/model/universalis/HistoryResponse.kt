package com.dkosub.ffxiv.tools.model.universalis

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class HistoryResponse(
    val items: List<HistoryItem>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HistoryItem(
    val itemID: Int,
    val nqSaleVelocity: Double,
    val hqSaleVelocity: Double,
)
