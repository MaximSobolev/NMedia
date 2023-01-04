package ru.netology.firstask.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.firstask.dto.Post
import ru.netology.firstask.model.PhotoModel

interface PostRepository {
    val data : Flow<PagingData<Post>>
    suspend fun likeByIdAsync(post: Post)
    suspend fun shareByIdAsync(id: Long)
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
    suspend fun saveWithAttachments(post: Post, photoModel: PhotoModel)
}