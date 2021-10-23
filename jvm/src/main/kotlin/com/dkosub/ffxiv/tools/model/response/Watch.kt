package com.dkosub.ffxiv.tools.model.response

data class Watch(
    val itemId: Int,
    val name: String,
    val datacenterMinimum: Int,
    val datacenterMean: Int,
    val datacenterDeviation: Double,
    val worldMinimum: Int,
    val worldMean: Int,
    val worldDeviation: Double,
    val materials: List<Material>
)
