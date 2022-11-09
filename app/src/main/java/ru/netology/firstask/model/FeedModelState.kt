package ru.netology.firstask.model

data class FeedModelState (
    val idle : Boolean = false,
    val loading : Boolean = false,
    val error : Boolean = false,
    val refreshing : Boolean = false
        )