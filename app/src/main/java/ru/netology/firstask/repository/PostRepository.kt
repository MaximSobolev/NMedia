package ru.netology.firstask.repository

import ru.netology.firstask.dto.Post

interface PostRepository {
    fun getAllAsync(callback : NMediaCallback<List<Post>>)
    fun likeByIdAsync(post: Post, callback: NMediaCallback<Post>)
    fun shareByIdAsync(id: Long, callback: NMediaCallback<Post>)
    fun removeByIdAsync(id: Long, callback: NMediaCallback<Unit>)
    fun saveAsync(post: Post, callback: NMediaCallback<Post>)
}