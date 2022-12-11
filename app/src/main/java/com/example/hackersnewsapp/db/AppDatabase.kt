package com.example.hackersnewsapp.db

import android.content.Context
import androidx.room.*
import com.example.hackersnewsapp.dao.AppDao
import com.example.hackersnewsapp.model.TopStories


@Database(entities = [TopStoryEntity::class], version = 1)
@TypeConverters(TopStoriesTypeConverter::class)
abstract class AppDatabase : RoomDatabase(){

    abstract fun appDao(): AppDao

    companion object{
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

        /* synchronized(AppDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "database"
                    ).build()
                }
            }*/

            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "database"
                ).build()
            }

            return INSTANCE!!
        }
    }
}

