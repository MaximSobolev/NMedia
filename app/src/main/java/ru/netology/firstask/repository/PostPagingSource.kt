package ru.netology.firstask.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.netology.firstask.dto.Post
import ru.netology.firstask.error.ApiError
import ru.netology.firstask.retrofit.PostApiService

class PostPagingSource(
    private val postApiService: PostApiService
) : PagingSource<Long, Post>() {
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val response = when (params) {
                is LoadParams.Refresh -> {
                    postApiService.getLatest(params.loadSize)
                }
                is LoadParams.Append -> {
                    postApiService.getBefore(id = params.key, count = params.loadSize)
                }
                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(), nextKey = null, prevKey = params.key
                )
            }

            if (!response.isSuccessful) {
                throw HttpException(response)
            }

            val data = response.body() ?: throw ApiError(response.code(), response.message())
            return LoadResult.Page(
                data = data,
                prevKey = params.key,
                nextKey = data.lastOrNull()?.id
            )
        } catch (e : Exception) {
            return LoadResult.Error(e)
        }
    }
}