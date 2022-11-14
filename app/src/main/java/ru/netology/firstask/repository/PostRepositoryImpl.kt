package ru.netology.firstask.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.netology.firstask.dto.Post
import ru.netology.firstask.retrofit.PostApi
import ru.netology.firstask.dao.PostDao
import ru.netology.firstask.entity.PostEntity
import ru.netology.firstask.entity.toDto
import ru.netology.firstask.entity.toEntity
import ru.netology.firstask.error.*
import java.io.IOException
import java.util.concurrent.CancellationException
import kotlin.Exception

class PostRepositoryImpl(
    private val dao : PostDao): PostRepository {
    override val data : Flow<List<Post>> = dao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAllAsync() {
        try {
            val response = PostApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            val posts = body.map {
                it.copy(localId = it.id, uploadedOnServer = true, displayOnScreen = true)
            }
            dao.insert(posts.toEntity())
        } catch (e : IOException) {
            throw NetworkError()
        } catch (e : Exception) {
            throw UnknownError()
        }
    }

    override fun getNewerCount(id : Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000)
                val response = PostApi.retrofitService.getNewer(id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                var body = response.body() ?: throw ApiError(response.code(), response.message())
                body = body.map {
                    it.copy(displayOnScreen = false, uploadedOnServer = true)
                }
                dao.insert(body.toEntity())
                emit(body.size)
            } catch (e : CancellationException) {
                throw e
            } catch (e : IOException) {
                throw NetworkError()
            } catch (e : Exception) {
                throw UnknownError()
            }
        }
    }.flowOn(Dispatchers.Default)


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

    override suspend fun displayNewerPosts() {
        dao.displayPosts()
    }

}