package ru.netology.firstask.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.firstask.dto.PushToken
import ru.netology.firstask.model.AuthState

interface UserApiService {
    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun signIn(@Field("login") login : String, @Field("pass") pass : String) : Response<AuthState>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun signUp(
        @Field("login") login : String,
        @Field("pass") pass : String,
        @Field("name") name : String
    ) : Response<AuthState>

    @Multipart
    @POST("users/registration")
    suspend fun signUpWithAvatar(
        @Part("login") login : RequestBody,
        @Part("pass") pass : RequestBody,
        @Part("name") name : RequestBody,
        @Part media : MultipartBody.Part
    ) : Response<AuthState>

    @POST ("users/push-tokens")
    suspend fun saveToken(@Body pushToken : PushToken) : Response<Unit>
}