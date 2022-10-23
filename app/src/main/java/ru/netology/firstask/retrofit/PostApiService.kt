package ru.netology.firstask.retrofit

import retrofit2.Call
import retrofit2.http.*
import ru.netology.firstask.dto.Post

interface PostApiService {
    @GET("posts")
    fun getAll(): Call<List<Post>>

    @POST("posts")
    fun save(@Body post: Post) : Call<Post>

    @DELETE("posts/{id}")
    fun removeById(@Path("id") id : Long) : Call<Unit>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id : Long) : Call<Post>

    @DELETE("posts/{id}/likes")
    fun dislikeById(@Path("id") id : Long) : Call<Post>
}