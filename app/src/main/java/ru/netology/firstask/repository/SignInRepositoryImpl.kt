package ru.netology.firstask.repository

import ru.netology.firstask.error.ApiError
import ru.netology.firstask.error.NetworkError
import ru.netology.firstask.model.AuthState
import ru.netology.firstask.retrofit.PostApi
import java.io.IOException

class SignInRepositoryImpl : SignInRepository {
    override suspend fun signIn(login: String, pass: String): AuthState {
        try {
            val response = PostApi.userService.signIn(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e : ApiError) {
            throw ApiError(e.status, e.code)
        } catch (e : IOException) {
            throw NetworkError()
        } catch (e : Exception) {
            throw UnknownError()
        }
    }

}