package ru.netology.firstask.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.firstask.dto.Post

@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val author : String,
    val content : String,
    val published : String,
    val videoUrl : String? = null,
    val videoName : String? = null,
    val videoViewCount: Int? = null,
    val likes : Int = 1499999,
    val share : Int = 9599,
    val view : Int = 10500,
    val likeByMe : Boolean = false
        ) {
    fun toDto() = Post(id, author, content, published, videoUrl, videoName,
        videoViewCount, likes, share, view, likeByMe)

    companion object {
        fun fromDto(post : Post) = PostEntity(post.id, post.author, post.content, post.published, post.videoUrl, post.videoName,
            post.videoViewCount, post.like, post.share, post.view, post.likeByMe)
    }
}
