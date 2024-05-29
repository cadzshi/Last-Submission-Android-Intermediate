package com.dicoding.midsubmissionintermediate.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.midsubmissionintermediate.data.pref.UserModel
import com.dicoding.midsubmissionintermediate.data.remote.response.ListStoryItem
import com.dicoding.midsubmissionintermediate.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading : LiveData<Boolean> = _showLoading

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
        }
    }
    val story: LiveData<PagingData<ListStoryItem>> =
        repository.getStory().cachedIn(viewModelScope)
}