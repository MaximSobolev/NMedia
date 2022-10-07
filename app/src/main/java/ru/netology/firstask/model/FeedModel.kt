package ru.netology.firstask.model

import ru.netology.firstask.dto.Post

data class FeedModel (
    val posts : List<Post> = emptyList(),
    val loading : Boolean = false,
    val error : Boolean = false,
    val empty : Boolean = false
        )