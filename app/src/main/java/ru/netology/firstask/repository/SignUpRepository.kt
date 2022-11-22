package ru.netology.firstask.repository

import ru.netology.firstask.model.AuthState
import ru.netology.firstask.model.PhotoModel

interface SignUpRepository {
    suspend fun signUp(name : String, login : String, pass : String) : AuthState
    suspend fun signUpWithAvatar(name : String, login : String, pass : String, photoModel: PhotoModel) : AuthState
}