package ru.netology.firstask.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.firstask.R
import ru.netology.firstask.db.AppDb
import ru.netology.firstask.dto.Post
import ru.netology.firstask.error.*
import ru.netology.firstask.model.FeedModel
import ru.netology.firstask.model.FeedModelState
import ru.netology.firstask.error.OperationType
import ru.netology.firstask.repository.PostRepository
import ru.netology.firstask.repository.PostRepositoryImpl
import java.lang.Exception
import kotlin.math.floor

private val empty = Post (
    localId = 0,
    id = 0,
    author = "Me",
    authorAvatar = "",
    content = "",
    published = ""
        )

class PostViewModel(application : Application) : AndroidViewModel(application) {
    private val repository : PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao
    )
    val data : LiveData<FeedModel> = repository.data
        .map {FeedModel(it, it.isEmpty())}
        .asLiveData(Dispatchers.Default)

    val newerCount : LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .asLiveData(Dispatchers.Default)
    }

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState : LiveData<FeedModelState>
        get() = _dataState
    private val edited = MutableLiveData (empty)
    private var draft = ""
    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage : LiveData<Int>
            get() = _errorMessage
    private val error = MutableLiveData<HandlerError>()


    init {
        loadPosts()
    }

    fun likeById(post: Post) {
        viewModelScope.launch {
            try {
                repository.likeByIdAsync(post)
            } catch (e : AppError) {
                errorProcessing(HandlerError(OperationType.LIKE, post, e))
            }
        }
    }
    fun shareById(id : Long) {
        viewModelScope.launch {
            try {
                repository.shareByIdAsync(id)
            } catch (e : AppError) {
                errorProcessing(HandlerError(OperationType.SHARE, Post(0,id), e))
            }
        }
    }
    fun removeById(id : Long) {
        viewModelScope.launch {
            try {
                repository.removeByIdAsync(id)
            } catch (e : AppError) {
                errorProcessing(HandlerError(OperationType.REMOVE, Post(0,id), e))
            }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            _dataState.value = FeedModelState(loading = true)
            try {
                repository.getAllAsync()
                _dataState.value = FeedModelState(idle = true)
            } catch (e : Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            _dataState.value = FeedModelState(refreshing = true)
            try {
                repository.getAllAsync()
                _dataState.value = FeedModelState(idle = true)
            } catch (e : Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun save () {
        viewModelScope.launch {
            edited.value?.let {
                try {
                    repository.saveAsync(it)
                } catch (e : AppError) {
                    val post = edited.value ?: return@let
                    errorProcessing(HandlerError(OperationType.SAVE, post, e))
                }
            }
            edited.postValue(empty)
        }
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

    private fun errorProcessing (handler : HandlerError) {
        when (handler.error) {
            is ApiError -> {
                error.postValue(handler)
                _errorMessage.postValue(R.string.retry_text)
            }
            is NetworkError -> {
                error.postValue(handler)
                _errorMessage.postValue(R.string.network_problems)

            }
            is UnknownError -> {
                error.postValue(handler)
                _errorMessage.postValue(R.string.unknown_problems)
            }
        }
    }

    fun retryOperation () {
        val handler = error.value ?: return
        when (handler.operation) {
            OperationType.LIKE -> {
                likeById(handler.argument)
            }
            OperationType.SHARE -> {
                shareById(handler.argument.id)
            }
            OperationType.SAVE -> {
                edit(handler.argument)
                save()
            }
            OperationType.REMOVE -> {
                removeById(handler.argument.id)
            }
        }
    }

    fun displayNewerPosts() {
        viewModelScope.launch {
            repository.displayNewerPosts()
        }
    }
}