package ru.netology.firstask.hilt

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.firstask.retrofit.UserApiService

@InstallIn(SingletonComponent::class)
@EntryPoint
interface AppAuthEntryPoint {
    fun getUserService(): UserApiService
}