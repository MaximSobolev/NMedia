package ru.netology.firstask.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.firstask.dto.Post


class PostReposytoryInMemoryImpl : PostRepository {
    private var post = Post (
        1,
        "Нетология. Университет интернет-профессий будущего",
        "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        "21 мая в 18:36"
    )

    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        post = post.copy(likeByMe = !post.likeByMe)
        if (post.likeByMe) {
            post = post.copy(like = post.like + 1)
        } else {
            post = post.copy(like = post.like - 1)
        }
        data.value = post
    }

    override fun share() {
        post = post.copy(share = post.share + 1)
        data.value = post
    }
}