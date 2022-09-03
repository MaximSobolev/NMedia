package ru.netology.firstask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.firstask.db.AppDb
import ru.netology.firstask.dto.Post
import ru.netology.firstask.repository.PostRepository
import ru.netology.firstask.repository.PostRepositorySQLiteImpl
import kotlin.math.floor

private val empty = Post (
    id = 0,
    author = "",
    content = "",
    published = ""
        )

class PostViewModel(application : Application) : AndroidViewModel(application) {
    private val repository : PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )
    private var draft = ""
    val data = repository.getAll()

    val edited = MutableLiveData (empty)
    fun likeById(id : Long) = repository.likeById(id)
    fun shareById(id : Long) = repository.shareById(id)
    fun removeById(id : Long) = repository.removeById(id)


    fun save () {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun changeContent (content : String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value = it.copy(content = text)
        }
    }

    fun edit (post: Post) {
        edited.value = post
    }

    fun largeNumberDisplay (number : Int) : String {
        val firstBorder = 1_000
        val secondBorder = 10_000
        val thirdBorder = 1_000_000
        val numberOfSings = 10.0
        val output = when {
            (number >= firstBorder) and (number < secondBorder) ->
                "${String.format("%.1f",
                    floor((number/firstBorder.toDouble())*numberOfSings) /
                            numberOfSings)}K"
            (number >= secondBorder) and (number < thirdBorder) ->
                "${number/firstBorder}K"
            number >= thirdBorder -> "${String.format("%.1f",
                floor((number/thirdBorder.toDouble())*numberOfSings) /
                        numberOfSings)}M"
            else -> number.toString()
        }
        return output
    }

    fun showPreviewVideo (post : Post) : Boolean {
        if (post.videoUrl == null) return false
        return true
    }

    fun setDraft (content : String) {
        draft = content
    }
    fun getDraft() : String {
        val content = draft
        draft = ""
        return content
    }
}