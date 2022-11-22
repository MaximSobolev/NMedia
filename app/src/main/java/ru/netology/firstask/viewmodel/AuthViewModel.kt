package ru.netology.firstask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netology.firstask.model.AuthState
import ru.netology.firstask.sharedPreferences.AppAuth

class AuthViewModel : ViewModel() {
    val data : LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated : Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L
}