package ru.netology.firstask.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.firstask.repository.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    fun bindsSignInRepository(impl: SignInRepositoryImpl): SignInRepository

    @Singleton
    @Binds
    fun bindsSignUpRepository(impl: SignUpRepositoryImpl): SignUpRepository
}