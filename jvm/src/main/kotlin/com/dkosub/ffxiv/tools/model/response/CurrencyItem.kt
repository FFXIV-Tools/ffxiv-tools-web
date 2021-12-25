package com.dkosub.ffxiv.tools.model.response

data class CurrencyItem(
    val id: Int,
    val name: String,
    val icon: Int,
    val cost: Int,
    val quantity: Int,
    val datacenterMinimum: Int,
    val datacenterMean: Int,
    val datacenterDeviation: Double,
    val worldMinimum: Int,
    val worldMean: Int,
    val worldDeviation: Double,
)
