package com.example.hackersnewsapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hackersnewsapp.db.TopStoryEntity
import com.example.hackersnewsapp.model.TopStories

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(topStory: TopStoryEntity)

    @Query("SELECT * FROM TopStoryEntity")
    suspend fun getAllTopStory(): TopStoryEntity
}