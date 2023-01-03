package ru.netology.firstask.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.firstask.attachment.AttachmentEmbeddable
import ru.netology.firstask.attachment.AttachmentType
import ru.netology.firstask.dto.Post
import ru.netology.firstask.dao.PostDao
import ru.netology.firstask.dto.Media
import ru.netology.firstask.entity.PostEntity
import ru.netology.firstask.entity.toEntity
import ru.netology.firstask.error.*
import ru.netology.firstask.model.PhotoModel
import ru.netology.firstask.retrofit.PostApiService
import java.io.IOException
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.Exception

class PostRepositoryImpl @Inject constructor(
    private val dao : PostDao,
    private val postApiService: PostApiService
    ): PostRepository {
//    override val data : Flow<List<Post>> = dao.getAll()
//        .map(List<PostEntity>::toDto)
//        .flowOn(Dispatchers.Default)

    override val data = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            PostPagingSource(postApiService)
        }
    ).flow

    override suspend fun getAllAsync() {
        try {
            val response = postApiService.getAll()
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
                val response = postApiService.getNewer(id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                var body = response.body() ?: throw ApiError(response.code(), response.message())
                body = body.map {
                    it.copy(localId = it.id, displayOnScreen = false, uploadedOnServer = true)
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
                postApiService.dislikeById(post.id)
            } else {
                postApiService.likeById(post.id)
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
            val response = postApiService.removeById(id)
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
           dao.save(PostEntity.fromDto(post).copy(uploadedOnServer = false, displayOnScreen = true))
        try {
            val response = postApiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body).copy(localId = body.id, uploadedOnServer = true, displayOnScreen = true))
        } catch (e : IOException) {
            throw NetworkError()
        } catch (e : Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveWithAttachments(post: Post, photoModel: PhotoModel) {
        try {
            val media = upload(photoModel)
            dao.save(PostEntity.fromDto(
                post.copy(attachment = AttachmentEmbeddable(url = media.id, type = AttachmentType.IMAGE),
                    uploadedOnServer = false, displayOnScreen = true)))
            val response = postApiService.save(
                post.copy(attachment = AttachmentEmbeddable(url = media.id, type = AttachmentType.IMAGE)))
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body).copy(localId = body.id, uploadedOnServer = true, displayOnScreen = true))
        } catch (e : IOException) {
            throw NetworkError()
        } catch (e : Exception) {
            throw UnknownError()
        }
    }

    private suspend fun upload (photoModel: PhotoModel) : Media {
        try {
            val response = postApiService.upload(
                MultipartBody.Part.createFormData(
                    "file",
                    photoModel.file?.name,
                    requireNotNull(photoModel.file?.asRequestBody())
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
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