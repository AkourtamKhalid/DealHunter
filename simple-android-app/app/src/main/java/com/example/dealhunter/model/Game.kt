package com.example.dealhunter.model

data class Game(
    val gameID: String,
    val steamAppID: String? = null,
    val cheapest: String,
    val cheapestDealID: String,
    val external: String,
    val internalName: String,
    val thumb: String
)