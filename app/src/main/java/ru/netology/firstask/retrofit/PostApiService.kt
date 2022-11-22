package ru.netology.firstask.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.firstask.dto.Media
import ru.netology.firstask.dto.Post
import ru.netology.firstask.model.AuthState

interface PostApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post) : Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id : Long) : Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id : Long) : Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id : Long) : Response<Post>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long) : Response<List<Post>>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media : MultipartBody.Part) : Response<Media>

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
}