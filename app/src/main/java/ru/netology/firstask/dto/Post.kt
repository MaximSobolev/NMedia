package ru.netology.firstask.dto

import ru.netology.firstask.attachment.AttachmentEmbeddable
import java.io.Serializable

data class Post (
    val id : Long,
    val author : String,
    val authorAvatar: String,
    val content : String,
    val published : String,
    val likes : Int = 1499999,
    val share : Int = 9599,
    val view : Int = 10500,
    val likedByMe : Boolean = false,
    var attachment: AttachmentEmbeddable? = null
) : Serializable