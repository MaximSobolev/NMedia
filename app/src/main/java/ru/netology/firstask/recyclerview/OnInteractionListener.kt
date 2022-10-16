package ru.netology.firstask.recyclerview

import ru.netology.firstask.dto.Post

interface OnInteractionListener {
    fun onLike(post : Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun showVideo(post : Post)
    fun openPost(post : Post)
}