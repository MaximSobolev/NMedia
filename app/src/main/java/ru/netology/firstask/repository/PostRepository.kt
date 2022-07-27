package ru.netology.firstask.repository

import androidx.lifecycle.LiveData
import ru.netology.firstask.dto.Post

interface PostRepository {
    fun get() : LiveData<Post>
    fun like()
    fun share()
}