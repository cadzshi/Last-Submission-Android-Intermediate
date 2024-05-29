package com.dicoding.midsubmissionintermediate.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.midsubmissionintermediate.data.pref.UserModel
import com.dicoding.midsubmissionintermediate.data.remote.response.ListStoryItem
import com.dicoding.midsubmissionintermediate.data.remote.response.StoryResponse
import com.dicoding.midsubmissionintermediate.data.remote.retrofit.ApiConfig
import com.dicoding.midsubmissionintermediate.data.repository.UserRepository
import com.dicoding.midsubmissionintermediate.view.login.LoginViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    private val _dataLocationStories = MutableLiveData<ArrayList<ListStoryItem>>()
    val dataLocationStories : LiveData<ArrayList<ListStoryItem>> = _dataLocationStories

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getLocationStories(token: String){
        viewModelScope.launch {
            val client = ApiConfig.getApiService(token).getStoriesWithLocation()
            client.enqueue(object : Callback<StoryResponse> {
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _dataLocationStories.value = ArrayList(responseBody.listStory)
                        }
                    } else {
                        Log.e(LoginViewModel.TAG, "onFailure: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    Log.e(LoginViewModel.TAG, "onFailure: ${t.message}")
                }
            })
        }
    }

}