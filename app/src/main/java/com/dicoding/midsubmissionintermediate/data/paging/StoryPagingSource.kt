package com.dicoding.midsubmissionintermediate.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.midsubmissionintermediate.data.pref.UserPreference
import com.dicoding.midsubmissionintermediate.data.remote.response.ListStoryItem
import com.dicoding.midsubmissionintermediate.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first

class StoryPagingSource(
    private val apiConfig: ApiConfig,
    private val preference: UserPreference
) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = preference.getSession().first().token
            val response = apiConfig.getApiService(token).getStories(position, params.loadSize).listStory

            LoadResult.Page(
                data = response,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (response.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}


//