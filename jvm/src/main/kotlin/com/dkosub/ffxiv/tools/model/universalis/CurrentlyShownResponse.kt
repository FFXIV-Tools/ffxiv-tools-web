package com.dkosub.ffxiv.tools.model.universalis

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrentlyShownResponse(
    val items: List<CurrentlyShownItem>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrentlyShownItem(
    val itemID: Int,
    val listings: List<Listing>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Listing(
    val worldID: Int,
    val pricePerUnit: Int,
    val quantity: Int
)
