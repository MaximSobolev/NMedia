package ru.netology.firstask.dao

import ru.netology.firstask.dto.Post

interface PostDao {
    fun getAll() : List<Post>
    fun likeById(id : Long)
    fun shareById(id : Long)
    fun removeById(id :Long)
    fun save(post : Post) : Post
}