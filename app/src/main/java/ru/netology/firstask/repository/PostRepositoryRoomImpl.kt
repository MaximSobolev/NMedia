package ru.netology.firstask.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.firstask.dao.PostDao
import ru.netology.firstask.dto.Post
import ru.netology.firstask.entity.PostEntity

class PostRepositoryRoomImpl(
    private val dao : PostDao
) : PostRepository {

    override fun getAll(): LiveData<List<Post>> = dao.getAll().map { list ->
        list.map { it.toDto() }
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }
}