package ru.netology.firstask.dto

import java.io.Serializable

data class Post (
    val id : Long,
    val author : String,
    val content : String,
    val published : String,
    val videoUrl : String? = null,
    val videoName : String? = null,
    val videoViewCount: Int? = null,
    val like : Int = 1499999,
    val share : Int = 9599,
    val view : Int = 10500,
    val likeByMe : Boolean = false
) : Serializable