package ru.netology.firstask.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.firstask.dto.Post
import ru.netology.firstask.retrofit.PostApi
import java.lang.Exception
import ru.netology.firstask.dao.PostDao
import ru.netology.firstask.entity.PostEntity
import ru.netology.firstask.entity.toDto
import ru.netology.firstask.entity.toEntity
import ru.netology.firstask.error.*
import java.io.IOException

class PostRepositoryImpl(
    private val dao : PostDao): PostRepository {
    override val data: LiveData<List<Post>> = dao.getAll().map(List<PostEntity>::toDto)

    override suspend fun getAllAsync() {
        try {
            val response = PostApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            val posts = body.map {
                it.copy(localId = it.id, uploadedOnServer = true)
            }
            dao.insert(posts.toEntity())
        } catch (e : IOException) {
            throw NetworkError()
        } catch (e : Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeByIdAsync(post: Post) {
        dao.likeById(post.id)
        try {
            val response = if (post.likedByMe) {
                PostApi.retrofitService.dislikeById(post.id)
            } else {
                PostApi.retrofitService.likeById(post.id)
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e : IOException) {
            throw NetworkError()
        } catch (e : Exception) {
            throw UnknownError()
        }
    }

    override suspend fun shareByIdAsync(id: Long) {
    }

    override suspend fun removeByIdAsync(id: Long) {
        dao.removeById(id)
        try {
            val response = PostApi.retrofitService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e : IOException) {
            throw NetworkError()
        } catch (e : Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveAsync(post: Post) {
           dao.save(PostEntity.fromDto(post).copy(uploadedOnServer = false))
        try {
            val response = PostApi.retrofitService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body).copy(localId = body.id, uploadedOnServer = true))
        } catch (e : IOException) {
            throw NetworkError()
        } catch (e : Exception) {
            throw UnknownError()
        }
    }

}