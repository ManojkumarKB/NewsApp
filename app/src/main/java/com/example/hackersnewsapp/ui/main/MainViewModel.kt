package com.example.hackersnewsapp.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.hackersnewsapp.db.AppDatabase
import com.example.hackersnewsapp.db.TopStoryEntity
import com.example.hackersnewsapp.repository.TopStoryRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(app: Application) : AndroidViewModel(app) {

    /*fun insertData(model:TopStories){
        GlobalScope.launch {
            getDatabase(AppDatabase.context).appDao().insertItems(model)
        }
    }*/

    private var topStoryLiveData = MutableLiveData<TopStoryEntity>()


    fun insertTopStory(entity: TopStoryEntity){
        val topStoryDao = AppDatabase.getDatabase(getApplication()).appDao()
        GlobalScope.launch {
            topStoryDao?.insertItems(entity)
            getTopStoriesFromRoomDb()
        }
    }

    fun getTopStoriesFromRoomDb() {
        val topStoryDao = AppDatabase.getDatabase((getApplication()))?.appDao()
        GlobalScope.launch {
            val list = topStoryDao?.getAllTopStory()
            topStoryLiveData.postValue(list?.let {
                it
            }?:TopStoryEntity(1,ArrayList()))
        }

    }

    fun getTopStoriesFromApi() {

        TopStoryRepository.api.getTopStories().enqueue(object  : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.body()!=null){
                    insertTopStory(TopStoryEntity(1,response.body()!!))
                }
                else{
                    return
                }
            }
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.d("MainViewModel",t.message.toString())
            }
        })
    }
    fun observeTopStroryLiveData() : LiveData<TopStoryEntity> {
        return topStoryLiveData
    }

}