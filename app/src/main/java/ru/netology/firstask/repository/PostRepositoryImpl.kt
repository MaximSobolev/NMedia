package ru.netology.firstask.repository

import androidx.paging.*
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.firstask.attachment.AttachmentEmbeddable
import ru.netology.firstask.attachment.AttachmentType
import ru.netology.firstask.dto.Post
import ru.netology.firstask.dao.PostDao
import ru.netology.firstask.dao.PostRemoteKeyDao
import ru.netology.firstask.db.AppDb
import ru.netology.firstask.dto.Ad
import ru.netology.firstask.dto.FeedItem
import ru.netology.firstask.dto.Media
import ru.netology.firstask.entity.PostEntity
import ru.netology.firstask.error.*
import ru.netology.firstask.model.PhotoModel
import ru.netology.firstask.retrofit.PostApiService
import java.io.IOException
import javax.inject.Inject
import kotlin.Exception
import kotlin.random.Random

class PostRepositoryImpl @Inject constructor(
    private val dao : PostDao,
    appDb: AppDb,
    postRemoteKeyDao: PostRemoteKeyDao,
    private val postApiService: PostApiService
    ): PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data : Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = PostRemoteMediator(postApiService, dao, postRemoteKeyDao, appDb),
        pagingSourceFactory = dao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
            .insertSeparators { prev, _  ->
                if (prev?.id?.rem(10) == 0L) {
                    Ad(Random.nextLong(), "figma.jpg")
                } else {
                    null
                }
            }
    }


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
            dao.insert(PostEntity.fromDto(body).copy(uploadedOnServer = true, displayOnScreen = true))
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
            dao.insert(PostEntity.fromDto(body).copy(uploadedOnServer = true, displayOnScreen = true))
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

    override suspend fun findPostById(id: Long): Post? {
        val post = dao.findPostById(id) ?: return null
        return post.toDto()
    }

}