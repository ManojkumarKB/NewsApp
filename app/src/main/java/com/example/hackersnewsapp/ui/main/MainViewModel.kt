package com.example.hackersnewsapp.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hackersnewsapp.dao.AppDao
import com.example.hackersnewsapp.dao.AppResponse
import com.example.hackersnewsapp.dao.ResponseStatus
import com.example.hackersnewsapp.db.AppDatabase
import com.example.hackersnewsapp.db.TopStoryEntity
import com.example.hackersnewsapp.model.Story
import com.example.hackersnewsapp.repository.TopStoryRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private var topStoryLiveData = MutableLiveData<TopStoryEntity>()
    private var topStoryDao: AppDao = AppDatabase.getDatabase((getApplication())).appDao()

    fun insertTopStory(entity: TopStoryEntity) {
        viewModelScope.launch {
            topStoryDao.insertItems(entity)
            getTopStoriesFromRoomDb()
        }
    }

    fun getTopStoriesFromRoomDb() {

        viewModelScope.launch {
            val list = topStoryDao?.getAllTopStory()
            if ((list?.topStoryId == null) || (list.topStoryId?.isEmpty()!!)) {
                topStoryLiveData.postValue(TopStoryEntity(1, ArrayList()))
            } else {
                topStoryLiveData.postValue(list.let { it })
            }
        }

    }

    fun getTopStoriesFromApi() {

        TopStoryRepository.api.getTopStories().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        // if response is successfull, store data in room db
                        insertTopStory(TopStoryEntity(ResponseStatus.SUCCESS, response.body()!!))
                    } else {
                        //if response body coming null post it as Fail with message
                        val l = listOf("Something went wrong!")
                        topStoryLiveData.postValue(TopStoryEntity(ResponseStatus.FAIL, l))
                    }
                } else {
                    //if response is not successful post it as Fail with message
                    val l = listOf(response.message() ?: "Something went wrong!")
                    topStoryLiveData.postValue(TopStoryEntity(ResponseStatus.FAIL, l))
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                //is response is failure, passing the correct error message for user
                val errorMessage = when (t) {
                    is IOException -> "No internet connection"
                    is HttpException -> "Something went wrong!"
                    else -> t.localizedMessage
                }

                val l = listOf(errorMessage ?: "Something went wrong!")
                topStoryLiveData.postValue(TopStoryEntity(ResponseStatus.FAIL, l))
            }
        })
    }

    fun observeTopStoryLiveData(): LiveData<TopStoryEntity> {
        return topStoryLiveData
    }

    fun getStoryFromApi(id: String): MutableLiveData<AppResponse<Any>> {
        val responseData = MutableLiveData<AppResponse<Any>>()

        TopStoryRepository.api.getStory(id).enqueue(object : Callback<Story> {
            override fun onResponse(call: Call<Story>, response: Response<Story>) {
                if (response.isSuccessful) {
                    responseData.value = AppResponse.success(response.body() as Story)
                } else {
                    responseData.value =
                        AppResponse.failure(response.message() ?: "Someting went wrong")
                }
                Log.d("MainViewModel", response.body()?.title.toString())
            }

            override fun onFailure(call: Call<Story>, t: Throwable) {
                responseData.value = AppResponse.error(t)
            }
        })
        return responseData
    }

}