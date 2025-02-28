package com.example.dealhunter.model

data class Store(
    val storeID: String,
    val storeName: String,
    val isActive: Int,
    val images: StoreImages
)

data class StoreImages(
    val banner: String,
    val logo: String,
    val icon: String
)
