package ru.netology.firstask.repository

import java.lang.Exception

interface NMediaCallback <T> {
    fun onSuccess(item : T)
    fun onError(e : Exception)
}