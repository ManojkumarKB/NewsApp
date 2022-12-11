package com.example.hackersnewsapp.api

import com.example.hackersnewsapp.model.TopStories
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("topstories.json")
    fun getTopStories() : Call<List<String>>
}

//https://hacker-news.firebaseio.com/v0/topstories.json