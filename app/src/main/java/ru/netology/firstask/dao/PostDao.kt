package ru.netology.firstask.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.firstask.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll() : LiveData<List<PostEntity>>

    @Query("""
                UPDATE PostEntity SET
                likes = likes + CASE WHEN likeByMe THEN -1 ELSE 1 END,
                likeByMe = CASE WHEN likeByMe THEN 0 ELSE 1 END
                WHERE id = :id;
            """)
    fun likeById(id : Long)

    @Query("""
                UPDATE PostEntity SET
                share = share + 1
                WHERE id = :id;
            """)
    fun shareById(id : Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id :Long)

    @Insert
    fun insert(post: PostEntity)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    fun updateContentById(id : Long, content : String)

    fun save(post : PostEntity) = if (post.id == 0L) insert(post) else
        updateContentById(post.id, post.content)
}