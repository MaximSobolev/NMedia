package ru.netology.firstask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.firstask.dto.Post
import ru.netology.firstask.model.FeedModel
import ru.netology.firstask.repository.NMediaCallback
import ru.netology.firstask.repository.PostRepository
import ru.netology.firstask.repository.PostRepositoryImpl
import ru.netology.firstask.util.SingleLiveEvent
import java.lang.Exception
import kotlin.math.floor

private val empty = Post (
    id = 0,
    author = "Me",
    authorAvatar = "",
    content = "",
    published = ""
        )

class PostViewModel(application : Application) : AndroidViewModel(application) {
    private val repository : PostRepository = PostRepositoryImpl()
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

    fun likeById(post: Post) {
        repository.likeByIdAsync(post, object : NMediaCallback<Post>{
            override fun onSuccess(item: Post) {
                val oldListPosts = _data.value?.posts.orEmpty()
                val newListPosts = oldListPosts.map { post ->
                    if (item.id == post.id) {
                        post.copy(likes = item.likes, likedByMe = item.likedByMe)
                    } else post
                }
                _postList.postValue(newListPosts)
            }

            override fun onError(e: Exception) {
            }

        })
    }
    fun shareById(id : Long) {
        repository.shareByIdAsync(id, object : NMediaCallback<Post> {
            override fun onSuccess(item: Post) {
            }

            override fun onError(e: Exception) {
            }
        })
    }
    fun removeById(id : Long) {
        val old = data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
            )
        )
        repository.removeByIdAsync(id, object : NMediaCallback<Unit> {
            override fun onSuccess(item: Unit) {
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }

        })
    }

    fun loadPosts() {
            _data.postValue(FeedModel(loading = true))
            repository.getAllAsync(object : NMediaCallback<List<Post>> {
                override fun onSuccess(item: List<Post>) {
                    _data.postValue(FeedModel(posts = item, empty = item.isEmpty()))
                }
                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
    }

    fun save () {
        edited.value?.let {
            repository.saveAsync(it, object : NMediaCallback<Unit> {
                override fun onSuccess(item: Unit) {
                    _postCreated.postValue(Unit)
                }
                override fun onError(e: Exception) {
                }
            })
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

    fun setDraft (content : String) {
        draft = content
    }
    fun getDraft() : String {
        val content = draft
        draft = ""
        return content
    }
}