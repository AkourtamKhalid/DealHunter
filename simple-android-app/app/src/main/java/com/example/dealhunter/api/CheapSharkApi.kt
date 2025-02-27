package com.example.dealhunter.api

import com.example.dealhunter.model.Game
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CheapSharkApi {
    @GET("games")
    fun getGames(
        @Query("title") title: String,
        @Query("limit") limit: Int = 60
    ): Call<List<Game>>
}