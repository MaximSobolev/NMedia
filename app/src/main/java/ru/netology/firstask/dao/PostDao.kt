package ru.netology.firstask.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.firstask.entity.PostEntity
@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY localId DESC")
    fun getAll() : Flow<List<PostEntity>>

    @Query("""
                UPDATE PostEntity SET
                likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
                likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                WHERE id = :id;
            """)
    suspend fun likeById(id : Long)

    @Query("""
                UPDATE PostEntity SET
                share = share + 1
                WHERE id = :id;
            """)
    suspend fun shareById(id : Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id :Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id : Long, content : String)

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty() : Boolean

    suspend fun save(post : PostEntity) = if (post.id == 0L) insert(post) else
        updateContentById(post.id, post.content)

    @Query("""
                UPDATE PostEntity SET
                displayOnScreen = 1 
                WHERE displayOnScreen = 0
            """)
    suspend fun displayPosts ()
}