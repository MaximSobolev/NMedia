package ru.netology.firstask.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.firstask.dto.Post

interface PostRepository {
    val data : Flow<List<Post>>
    suspend fun getAllAsync()
    fun getNewerCount(id : Long) : Flow<Int>
    suspend fun likeByIdAsync(post: Post)
    suspend fun shareByIdAsync(id: Long)
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
    suspend fun displayNewerPosts()
}