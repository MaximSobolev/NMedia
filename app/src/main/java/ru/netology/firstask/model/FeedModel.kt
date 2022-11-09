package ru.netology.firstask.model

import ru.netology.firstask.dto.Post

data class FeedModel (
    val posts : List<Post> = emptyList(),
    val empty : Boolean = false
        )