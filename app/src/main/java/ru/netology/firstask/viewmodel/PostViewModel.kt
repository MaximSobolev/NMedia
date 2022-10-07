package ru.netology.firstask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.firstask.dto.Post
import ru.netology.firstask.model.FeedModel
import ru.netology.firstask.repository.PostRepository
import ru.netology.firstask.repository.PostRepositoryRoomImpl
import ru.netology.firstask.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.math.floor

private val empty = Post (
    id = 0,
    author = "Me",
    content = "",
    published = ""
        )

class PostViewModel(application : Application) : AndroidViewModel(application) {
    private val repository : PostRepository = PostRepositoryRoomImpl()
    private val _data = MutableLiveData(FeedModel())
    val data : LiveData<FeedModel>
            get() = _data
    val edited = MutableLiveData (empty)
    private val _postCreated =  SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
            get() = _postCreated
    private val _postList = MutableLiveData<List<Post>>()
    val postList: LiveData<List<Post>>
            get() = _postList
    private var draft = ""

    init {
        loadPosts()
    }

    fun likeById(id : Long) {
        thread {
            val updatedPost = repository.likeById(id)
            val oldListPosts = _data.value?.posts.orEmpty()
            val newListPosts = oldListPosts.map { post ->
                if (updatedPost.id == post.id) {
                    post.copy(likes = updatedPost.likes, likedByMe = updatedPost.likedByMe)
                } else post
            }
            _postList.postValue(newListPosts)
        }
    }
    fun shareById(id : Long) {
        thread {
            repository.shareById(id)
        }
    }
    fun removeById(id : Long) {
        thread {
            val old = data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e : IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun loadPosts() {
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e : IOException) {
                FeedModel(error = true)
            }.also { _data.postValue(it) }
        }
    }

    fun save () {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.postValue(empty)
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