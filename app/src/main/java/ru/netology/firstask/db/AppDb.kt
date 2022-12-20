package ru.netology.firstask.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.firstask.dao.PostDao
import ru.netology.firstask.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract val postDao : PostDao
}