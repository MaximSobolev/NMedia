package ru.netology.firstask.repository

import ru.netology.firstask.model.AuthState

interface SignInRepository {
    suspend fun signIn(login : String, pass : String) : AuthState
}