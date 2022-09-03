package ru.netology.firstask.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.firstask.dto.Post

class PostDaoImpl(private val db : SQLiteDatabase) : PostDao {
    companion object {
        val DDL = """
        CREATE TABLE ${PostColumns.TABLE} (
            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
            ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
            ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
            ${PostColumns.COLUMN_VIDEO_URL} TEXT DEFAULT NULL,
            ${PostColumns.COLUMN_VIDEO_NAME} TEXT DEFAULT NULL,
            ${PostColumns.COLUMN_VIDEO_VIEW_COUNT} INTEGER DEFAULT NULL,
            ${PostColumns.COLUMN_lIKES} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_SHARE} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_VIEW} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_LIKE_BY_ME} BOOLEAN NOT NULL DEFAULT 0
        );
        """.trimIndent()
    }
    object PostColumns {
        const val TABLE = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_VIDEO_URL = "videoUrl"
        const val COLUMN_VIDEO_NAME = "videoName"
        const val COLUMN_VIDEO_VIEW_COUNT = "videoViewCount"
        const val COLUMN_lIKES = "likes"
        const val COLUMN_SHARE = "share"
        const val COLUMN_VIEW = "view"
        const val COLUMN_LIKE_BY_ME = "likeByMe"
        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_AUTHOR,
            COLUMN_CONTENT,
            COLUMN_PUBLISHED,
            COLUMN_VIDEO_URL,
            COLUMN_VIDEO_NAME,
            COLUMN_VIDEO_VIEW_COUNT,
            COLUMN_lIKES,
            COLUMN_SHARE,
            COLUMN_VIEW,
            COLUMN_LIKE_BY_ME
        )
    }
    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun likeById(id: Long) {
        db.execSQL(
            """
                UPDATE posts SET
                likes = likes + CASE WHEN likeByMe THEN -1 ELSE 1 END,
                likeByMe = CASE WHEN likeByMe THEN 0 ELSE 1 END
                WHERE id = ?;
            """.trimIndent(), arrayOf(id)
        )
    }

    override fun shareById(id: Long) {
        db.execSQL(
            """
                UPDATE posts SET
                share = share + 1
                WHERE id = ?;
            """.trimIndent(), arrayOf(id)
        )
    }

    override fun removeById(id: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            if (post.id != 0L) {
                put(PostColumns.COLUMN_ID, post.id)
            }
            put(PostColumns.COLUMN_AUTHOR, "Me")
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(PostColumns.COLUMN_PUBLISHED, "now")
            put(PostColumns.COLUMN_VIDEO_URL, post.videoUrl)
            put(PostColumns.COLUMN_VIDEO_NAME, post.videoName)
            put(PostColumns.COLUMN_VIDEO_VIEW_COUNT, post.videoViewCount)
            put(PostColumns.COLUMN_lIKES, post.like)
            put(PostColumns.COLUMN_SHARE, post.share)
            put(PostColumns.COLUMN_VIEW, post.view)
            put(PostColumns.COLUMN_LIKE_BY_ME, post.likeByMe)
        }
        val id = db.replace(PostColumns.TABLE, null, values)
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    private fun map (cursor : Cursor) : Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                videoUrl = getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO_URL)),
                videoName = getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO_NAME)),
                videoViewCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO_VIEW_COUNT)),
                like = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_lIKES)),
                share = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SHARE)),
                view = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_VIEW)),
                likeByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKE_BY_ME)) !=0
            )
        }
    }
}