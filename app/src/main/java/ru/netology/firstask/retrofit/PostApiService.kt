package ru.netology.firstask.retrofit

import retrofit2.Response
import retrofit2.http.*
import ru.netology.firstask.dto.Post

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
}