package ru.netology.firstask.retrofit

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.firstask.dto.Media
import ru.netology.firstask.dto.Post

interface PostApiService {

    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count : Int): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post) : Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id : Long) : Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id : Long) : Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id : Long) : Response<Post>

    @GET("posts/{id}/before")
    suspend fun getBefore(@Path("id") id: Long, @Query("count") count : Int) : Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getAfter(@Path("id") id: Long, @Query("count") count : Int) : Response<List<Post>>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media : MultipartBody.Part) : Response<Media>
}