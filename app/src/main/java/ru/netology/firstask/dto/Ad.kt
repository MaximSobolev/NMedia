package ru.netology.firstask.dto

data class Ad (
    override val id : Long,
    val image : String
) : FeedItem