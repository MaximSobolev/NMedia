package ru.netology.firstask.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.firstask.attachment.AttachmentEmbeddable
import ru.netology.firstask.dto.Post

@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val localId : Long,
    val id : Long,
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
    val uploadedOnServer: Boolean = false
) {
    fun toDto() = Post(localId, id, author, authorAvatar, content, published, likes, share, view,
        likedByMe, attachment, uploadedOnServer)

    companion object {
        fun fromDto(post : Post) = PostEntity(post.localId, post.id, post.author, post.authorAvatar, post.content,
            post.published, post.likes, post.share, post.view, post.likedByMe, post.attachment, post.uploadedOnServer)
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)