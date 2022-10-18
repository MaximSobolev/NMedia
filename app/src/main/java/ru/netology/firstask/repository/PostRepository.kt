package ru.netology.firstask.repository

import ru.netology.firstask.dto.Post

interface PostRepository {
//    fun getAll() : List<Post>
//    fun likeById(id : Long): Post
//    fun shareById(id : Long)
//    fun removeById(id :Long)
//    fun save(post : Post)
    fun getAllAsync(callback : NMediaCallback<List<Post>>)
    fun likeByIdAsync(post: Post, callback: NMediaCallback<Post>)
    fun shareByIdAsync(id: Long, callback: NMediaCallback<Post>)
    fun removeByIdAsync(id: Long, callback: NMediaCallback<Unit>)
    fun saveAsync(post: Post, callback: NMediaCallback<Unit>)

}