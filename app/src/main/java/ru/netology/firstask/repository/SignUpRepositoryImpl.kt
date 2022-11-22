package ru.netology.firstask.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.firstask.error.ApiError
import ru.netology.firstask.error.NetworkError
import ru.netology.firstask.model.AuthState
import ru.netology.firstask.model.PhotoModel
import ru.netology.firstask.retrofit.PostApi
import java.io.IOException

class SignUpRepositoryImpl : SignUpRepository {
    override suspend fun signUp(name: String, login: String, pass: String): AuthState {
        try {
            val response = PostApi.retrofitService.signUp(login, pass, name)
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

    override suspend fun signUpWithAvatar(
        name: String,
        login: String,
        pass: String,
        photoModel: PhotoModel
    ): AuthState {
        try {
            val response = PostApi.retrofitService.signUpWithAvatar(
                login.toRequestBody("text/plain".toMediaType()),
                pass.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                MultipartBody.Part.createFormData(
                    "file",
                    photoModel.file?.name,
                    requireNotNull(photoModel.file?.asRequestBody())
                )
            )
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