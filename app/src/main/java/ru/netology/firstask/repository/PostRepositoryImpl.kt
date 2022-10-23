package ru.netology.firstask.repository

import retrofit2.Call
import ru.netology.firstask.dto.Post
import ru.netology.firstask.retrofit.PostApi
import java.lang.Exception
import retrofit2.Callback
import retrofit2.Response
import ru.netology.firstask.viewmodel.PostViewModel

class PostRepositoryImpl(viewModel: PostViewModel): PostRepository {
    private val myViewModel = viewModel
    override fun getAllAsync(callback: NMediaCallback<List<Post>>) {
        PostApi.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(
                    call: Call<List<Post>>, response: Response<List<Post>>
                ) {
                    if (response.isSuccessful) {
                        try {
                            callback.onSuccess(
                                response.body() ?: throw RuntimeException("body is null"))
                        } catch (e : Exception) {
                            callback.onError(e)
                        }
                    } else {
                        myViewModel.makeErrorToast(response.code())
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

        })
    }

    override fun likeByIdAsync(post: Post, callback: NMediaCallback<Post>) {

        if (post.likedByMe) {
            PostApi.retrofitService.likeById(post.id)
                .enqueue(object : Callback<Post> {
                    override fun onResponse(
                        call: Call<Post>, response: Response<Post>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                callback.onSuccess(
                                    response.body() ?: throw RuntimeException("body is null"))
                            } catch (e : Exception) {
                                callback.onError(e)
                            }
                        } else {
                            myViewModel.makeErrorToast(response.code())
                        }
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        callback.onError(RuntimeException(t))
                    }

                })
        } else {
            PostApi.retrofitService.dislikeById(post.id)
                .enqueue(object : Callback<Post> {
                    override fun onResponse(
                        call: Call<Post>, response: Response<Post>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                callback.onSuccess(
                                    response.body() ?: throw RuntimeException("body is null")
                                )
                            } catch (e: Exception) {
                                callback.onError(e)
                            }
                        } else {
                            myViewModel.makeErrorToast(response.code())
                        }
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        callback.onError(RuntimeException(t))
                    }
                })
        }
    }

    override fun shareByIdAsync(id: Long, callback: NMediaCallback<Post>) {
    }

    override fun removeByIdAsync(id: Long, callback: NMediaCallback<Unit>) {
        PostApi.retrofitService.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>, response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        try {
                            callback.onSuccess(
                                response.body() ?: throw RuntimeException("body is null")
                            )
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    } else {
                        myViewModel.makeErrorToast(response.code())
                    }
                }

                override fun onFailure(call:Call<Unit>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })
    }

    override fun saveAsync(post: Post, callback: NMediaCallback<Post>) {
        PostApi.retrofitService.save(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        try {
                            callback.onSuccess(
                                response.body() ?: throw RuntimeException("body is null")
                            )
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    } else {
                        myViewModel.makeErrorToast(response.code())
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })
    }
}