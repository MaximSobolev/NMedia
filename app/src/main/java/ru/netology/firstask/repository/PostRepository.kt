package ru.netology.firstask.repository

import ru.netology.firstask.dto.Post

interface PostRepository {
    fun getAll() : List<Post>
    fun likeById(id : Long): Post
    fun shareById(id : Long)
    fun removeById(id :Long)
    fun save(post : Post)
}