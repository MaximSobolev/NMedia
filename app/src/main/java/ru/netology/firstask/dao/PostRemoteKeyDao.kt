package ru.netology.firstask.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.firstask.entity.PostRemoteKeyEntity

@Dao
interface PostRemoteKeyDao {
    @Query("SELECT MAX(`key`) FROM PostRemoteKeyEntity")
    suspend fun max() : Long?

    @Query("SELECT MIN(`key`) FROM PostRemoteKeyEntity")
    suspend fun min() : Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key : PostRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<PostRemoteKeyEntity>)

    @Query("DELETE FROM PostRemoteKeyEntity")
    suspend fun clear()
}