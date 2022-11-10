package ru.netology.firstask.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.firstask.dao.PostDao
import ru.netology.firstask.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract val postDao : PostDao

    companion object {
        @Volatile
        private var instance : AppDb? = null

        fun getInstance(context : Context) : AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDataBase(context).also { instance = it }
            }
        }

        fun buildDataBase(context : Context) =
            Room.databaseBuilder(context, AppDb::class.java,"app.db")
                .build()
    }
}