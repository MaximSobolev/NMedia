package ru.netology.firstask.model

data class State (
    val idle : Boolean = false,
    val loading : Boolean = false,
    val error : Boolean = false
        )
