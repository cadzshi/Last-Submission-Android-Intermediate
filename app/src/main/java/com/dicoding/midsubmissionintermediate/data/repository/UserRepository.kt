package com.dicoding.midsubmissionintermediate.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.midsubmissionintermediate.data.paging.StoryPagingSource
import com.dicoding.midsubmissionintermediate.data.pref.UserModel
import com.dicoding.midsubmissionintermediate.data.pref.UserPreference
import com.dicoding.midsubmissionintermediate.data.remote.response.ListStoryItem
import com.dicoding.midsubmissionintermediate.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiConfig: ApiConfig
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiConfig ,userPreference)
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiConfig: ApiConfig
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiConfig)
            }.also { instance = it }
    }
}