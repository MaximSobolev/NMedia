package ru.netology.firstask.model

data class AuthState (
    val id : Long = 0,
    val token : String? = null,
    val avatar : String? = null
        )