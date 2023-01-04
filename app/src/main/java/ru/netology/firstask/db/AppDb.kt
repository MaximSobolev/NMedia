package ru.netology.firstask.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.firstask.dao.PostDao
import ru.netology.firstask.dao.PostRemoteKeyDao
import ru.netology.firstask.entity.PostEntity
import ru.netology.firstask.entity.PostRemoteKeyEntity

@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class],
    version = 1,
    exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao() : PostDao
    abstract fun postRemoteKeyDao() : PostRemoteKeyDao
}