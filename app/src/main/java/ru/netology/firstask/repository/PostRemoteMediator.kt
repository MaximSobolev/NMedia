package ru.netology.firstask.repository

import androidx.paging.*
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.firstask.dao.PostDao
import ru.netology.firstask.dao.PostRemoteKeyDao
import ru.netology.firstask.db.AppDb
import ru.netology.firstask.entity.PostEntity
import ru.netology.firstask.entity.PostRemoteKeyEntity
import ru.netology.firstask.error.ApiError
import ru.netology.firstask.retrofit.PostApiService

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val postApiService: PostApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    if (postDao.isEmpty()) {
                        postApiService.getLatest(state.config.pageSize)
                    } else {
                        val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                        postApiService.getAfter(id = id, count = state.config.pageSize)
                    }
                }
                LoadType.PREPEND -> return MediatorResult.Success(false)
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    postApiService.getBefore(id = id, count = state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw HttpException(response)
            }

            val data = response.body() ?: throw ApiError(response.code(), response.message())

            appDb.withTransaction {
                when(loadType) {
                    LoadType.REFRESH -> {
                        if (postDao.isEmpty()) {
                            postRemoteKeyDao.insert (
                                listOf(
                                    PostRemoteKeyEntity(
                                        PostRemoteKeyEntity.KeyType.AFTER,
                                        data.first().id
                                    ),
                                    PostRemoteKeyEntity(
                                        PostRemoteKeyEntity.KeyType.BEFORE,
                                        data.last().id
                                    )
                                )
                            )
                        } else {
                            postRemoteKeyDao.insert (
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.AFTER,
                                    data.first().id
                                )
                            )
                        }
                    }
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert (
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.BEFORE,
                                data.last().id
                            )
                        )
                    }
                    else -> return@withTransaction
                }
                postDao.insert(data.map(PostEntity::fromDto))
            }
            return MediatorResult.Success(data.isEmpty())
        } catch (e : Exception) {
            return MediatorResult.Error(e)
        }
    }
}