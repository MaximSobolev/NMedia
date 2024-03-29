package ru.netology.firstask.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.firstask.R
import ru.netology.firstask.dto.DraftPost
import ru.netology.firstask.dto.FeedItem
import ru.netology.firstask.dto.Post
import ru.netology.firstask.error.*
import ru.netology.firstask.model.FeedModelState
import ru.netology.firstask.error.OperationType
import ru.netology.firstask.model.AuthState
import ru.netology.firstask.model.PhotoModel
import ru.netology.firstask.repository.PostRepository
import ru.netology.firstask.sharedPreferences.AppAuth
import java.io.File
import javax.inject.Inject
import kotlin.math.floor

private val empty = Post(
    id = 0,
    author = "Me",
    authorAvatar = "",
    content = "",
    published = ""
)
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth
) : ViewModel() {
    private val cached = repository.data.cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedItem>> = appAuth
        .authStateFlow
        .flatMapLatest { cached }

    val authState : LiveData<AuthState> = appAuth.authStateFlow.asLiveData()

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    private val edited = MutableLiveData(empty)
    private var draft: DraftPost? = null
    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage
    private val error = MutableLiveData<HandlerError>()

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

    private val _changePost = MutableLiveData<Post?>(null)
    val changePost: LiveData<Post?>
        get() = _changePost

    fun likeById(post: Post) {
        viewModelScope.launch {
            try {
                repository.likeByIdAsync(post)
            } catch (e: AppError) {
                errorProcessing(HandlerError(OperationType.LIKE, post, e))
            }
        }
    }

    fun shareById(id: Long) {
        viewModelScope.launch {
            try {
                repository.shareByIdAsync(id)
            } catch (e: AppError) {
                errorProcessing(HandlerError(OperationType.SHARE, Post(0, id), e))
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeByIdAsync(id)
            } catch (e: AppError) {
                errorProcessing(HandlerError(OperationType.REMOVE, Post(0, id), e))
            }
        }
    }


    fun save() {
        viewModelScope.launch {
            edited.value?.let { post ->
                try {
                    photo.value?.let { photoModel ->
                        if (photoModel.file != null) {
                            repository.saveWithAttachments(post, photoModel)
                        } else {
                            repository.saveAsync(post)
                        }
                    } ?: repository.saveAsync(post)
                } catch (e: AppError) {
                    val postError = edited.value ?: return@let
                    val photo = photo.value
                    errorProcessing(HandlerError(OperationType.SAVE, postError, e, photo))
                }
            }
            edited.postValue(empty)
        }
    }

    fun changeContent(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value = it.copy(content = text)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    private fun editPhoto(photoModel: PhotoModel?) {
        _photo.postValue(photoModel)
    }

    fun largeNumberDisplay(number: Int): String {
        val firstBorder = 1_000
        val secondBorder = 10_000
        val thirdBorder = 1_000_000
        val numberOfSings = 10.0
        val output = when {
            (number >= firstBorder) and (number < secondBorder) ->
                "${
                    String.format(
                        "%.1f",
                        floor((number / firstBorder.toDouble()) * numberOfSings) /
                                numberOfSings
                    )
                }K"
            (number >= secondBorder) and (number < thirdBorder) ->
                "${number / firstBorder}K"
            number >= thirdBorder -> "${
                String.format(
                    "%.1f",
                    floor((number / thirdBorder.toDouble()) * numberOfSings) /
                            numberOfSings
                )
            }M"
            else -> number.toString()
        }
        return output
    }

    fun setDraft(content: String) {
        draft = DraftPost(content, photo.value)
        _photo.postValue(null)
    }

    fun getDraft(): DraftPost? {
        val draftPost = draft
        draft = null
        return draftPost
    }

    private fun errorProcessing(handler: HandlerError) {
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

    fun retryOperation() {
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
                editPhoto(handler.photo)
                save()
            }
            OperationType.REMOVE -> {
                removeById(handler.argument.id)
            }
        }
    }

    fun savePhoto(uri: Uri?, file: File?) {
        _photo.postValue(PhotoModel(uri, file))
    }

    fun findPostById(id: Long) {
        viewModelScope.launch {
            val post = repository.findPostById(id)
            _changePost.postValue(post)
        }
    }

}