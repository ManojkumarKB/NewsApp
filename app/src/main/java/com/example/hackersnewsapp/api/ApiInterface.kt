package com.example.hackersnewsapp.api

import com.example.hackersnewsapp.model.Story
import com.example.hackersnewsapp.model.TopStories
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("topstories.json")
    fun getTopStories() : Call<List<String>>

    @GET("item/{id}.json")
    fun getStory(@Path("id") id: String) : Call<Story>
}

//https://hacker-news.firebaseio.com/v0/topstories.json

//https://hacker-news.firebaseio.com/v0/item/