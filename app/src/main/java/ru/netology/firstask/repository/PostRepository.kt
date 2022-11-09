package ru.netology.firstask.repository

import androidx.lifecycle.LiveData
import ru.netology.firstask.dto.Post

interface PostRepository {
    val data : LiveData<List<Post>>
    suspend fun getAllAsync()
    suspend fun likeByIdAsync(post: Post)
    suspend fun shareByIdAsync(id: Long)
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
}