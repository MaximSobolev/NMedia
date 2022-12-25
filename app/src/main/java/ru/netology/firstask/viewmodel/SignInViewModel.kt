package ru.netology.firstask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.firstask.R
import ru.netology.firstask.error.ApiError
import ru.netology.firstask.error.AppError
import ru.netology.firstask.error.NetworkError
import ru.netology.firstask.error.UnknownError
import ru.netology.firstask.model.State
import ru.netology.firstask.repository.SignInRepository
import ru.netology.firstask.sharedPreferences.AppAuth
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: SignInRepository
) : ViewModel() {
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state
    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage
    @Inject
    lateinit var appAuth: AppAuth

    fun signIn(login: String, password: String) {
        _state.postValue(State(loading = true))
        viewModelScope.launch {
            try {
                val authState = repository.signIn(login, password)
                if (authState.id != 0L && authState.token != null) {
                    appAuth.setAuth(authState.id, authState.token, authState.avatar)
                }
                _state.postValue(State(idle = true))
            } catch (e: AppError) {
                errorProcessing(e)
                _state.postValue(State(error = true))
            }
        }
    }

    private fun errorProcessing(e: AppError) {
        when (e) {
            is ApiError -> {
                if (e.status == 400) {
                    _errorMessage.postValue(R.string.login_or_password_are_wrong)
                    return
                }
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