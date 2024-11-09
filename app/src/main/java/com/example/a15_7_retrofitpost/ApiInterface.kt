package com.example.a15_7_retrofitpost

import com.example.a15_7_retrofitpost.models.ApiData
import retrofit2.http.GET


interface ApiInterface {
    @GET("woof.json?ref=apilist.fun")
    suspend fun getRandomDog(): ApiData
}