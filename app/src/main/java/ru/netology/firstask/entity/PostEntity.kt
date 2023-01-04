package ru.netology.firstask.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.firstask.attachment.AttachmentEmbeddable
import ru.netology.firstask.dto.Post

@Entity
data class PostEntity (
    @PrimaryKey
    val id : Long,
    val authorId : Long,
    val author : String,
    val authorAvatar: String,
    val content : String,
    val published : String,
    val likes : Int = 0,
    val share : Int = 0,
    val view : Int = 0,
    val likedByMe : Boolean = false,
    @Embedded
    var attachment: AttachmentEmbeddable? = null,
    val uploadedOnServer: Boolean = false,
    val displayOnScreen : Boolean = true
) {
    fun toDto() = Post(id, authorId, author, authorAvatar, content, published, likes, share, view,
        likedByMe, attachment, uploadedOnServer, displayOnScreen)

    companion object {
        fun fromDto(post : Post) = PostEntity(post.id, post.authorId, post.author, post.authorAvatar,
            post.content, post.published, post.likes, post.share, post.view, post.likedByMe, post.attachment,
            post.uploadedOnServer, post.displayOnScreen)
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
