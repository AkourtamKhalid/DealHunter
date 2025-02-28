package com.example.dealhunter.api

import com.example.dealhunter.model.Deal
import com.example.dealhunter.model.Game
import com.example.dealhunter.model.Store
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CheapSharkApi {
    @GET("games")
    fun getGames(
        @Query("title") title: String,
        @Query("limit") limit: Int = 60
    ): Call<List<Game>>

    @GET("deals")
    fun getDeals(
        @Query("title") title: String,
        @Query("sortBy") sortBy: String = "Deal Rating",
        @Query("desc") desc: Int = 1,
        @Query("pageSize") pageSize: Int = 60,
        @Query("onSale") onSale: Int = 1
    ): Call<List<Deal>>

    @GET("deals")
    fun searchDeals(
        @Query("title") title: String,
        @Query("sortBy") sortBy: String = "Deal Rating",
        @Query("desc") desc: Int = 1
    ): Call<List<Deal>>

    @GET("stores")
    fun getStores(): Call<List<Store>>
}