package ru.netology.firstask.dto

import ru.netology.firstask.attachment.AttachmentEmbeddable
import java.io.Serializable

data class Post (
    override val id : Long = 0,
    val authorId : Long = 0,
    val author : String = "",
    val authorAvatar: String = "",
    val content : String = "",
    val published : String = "",
    val likes : Int = 0,
    val share : Int = 0,
    val view : Int = 0,
    val likedByMe : Boolean = false,
    var attachment: AttachmentEmbeddable? = null,
    val uploadedOnServer: Boolean = false,
    val displayOnScreen : Boolean = true,
    val ownedByMe : Boolean = false
) : Serializable, FeedItem