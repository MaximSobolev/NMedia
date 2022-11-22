package ru.netology.firstask.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.firstask.R
import ru.netology.firstask.error.ApiError
import ru.netology.firstask.error.AppError
import ru.netology.firstask.error.NetworkError
import ru.netology.firstask.error.UnknownError
import ru.netology.firstask.model.PhotoModel
import ru.netology.firstask.model.State
import ru.netology.firstask.repository.SignUpRepository
import ru.netology.firstask.repository.SignUpRepositoryImpl
import ru.netology.firstask.sharedPreferences.AppAuth
import java.io.File

class SignUpViewModel : ViewModel() {
    private val repository : SignUpRepository = SignUpRepositoryImpl()
    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo : LiveData<PhotoModel?>
        get() = _photo
    private val _state = MutableLiveData<State>()
    val state : LiveData<State>
        get() = _state
    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage : LiveData<Int>
        get() = _errorMessage

    fun savePhoto(uri : Uri?, file : File?) {
        _photo.postValue(PhotoModel(uri, file))
    }

    fun signUp(name : String, login : String, pass : String) {
        _state.postValue(State(loading = true))
        viewModelScope.launch {
            try {
                val authState = photo.value?.let {
                    repository.signUpWithAvatar(name, login, pass, it)
                } ?: repository.signUp(name, login, pass)
                if (authState.id != 0L && authState.token != null && authState.avatar != null) {
                    AppAuth.getInstance().setAuth(authState.id, authState.token, authState.avatar)
                }
                _state.postValue(State(idle = true))
                _photo.postValue(null)
            } catch (e : AppError) {
                errorProcessing(e)
                _state.postValue(State(error = true))
            }
        }
    }

    private fun errorProcessing(e : AppError) {
        when (e) {
            is ApiError -> {
                _errorMessage.postValue(R.string.retry_text)
            }
            is NetworkError -> {
                _errorMessage.postValue(R.string.network_problems)
            }
            is UnknownError -> {
                _errorMessage.postValue(R.string.unknown_problems)
            }
        }
    }
}