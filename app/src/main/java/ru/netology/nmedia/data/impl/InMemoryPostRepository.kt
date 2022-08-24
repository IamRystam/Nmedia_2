package ru.netology.nmedia.data.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Likes
import ru.netology.nmedia.Post
import ru.netology.nmedia.data.PostRepository
import ru.netology.nmedia.formatCount
import java.util.*

class InMemoryPostRepository : PostRepository {

    private var nextId = GENERATED_POSTS_AMOUNT.toLong()

    private val posts
        get() = checkNotNull(data.value) {
            "Data value should not be null"
        }

    override val data = MutableLiveData(List(GENERATED_POSTS_AMOUNT) { index ->
        val rightNow = Calendar.getInstance().time
        Post(
            id = index + 1L,
            author = "Автор",
            text = "Текст поста $index",
            date = rightNow.time,
            likes = Likes(count = 0, userLikes = false),
            reposts = 0,
            views = 0,
            video = "https://youtu.be/Ed0Xdi_xdfw"
        )
    }
    )


    override fun like(postId: Long) {

        data.value = posts.map {
            if (it.id != postId) it else {
                val currentLikes = checkNotNull(it.likes) {
                    "Data value should not be null"
                }
                val userLikes = !currentLikes.userLikes
                val count: Int = if (userLikes) {
                    currentLikes.count + 1
                } else {
                    currentLikes.count - 1
                }
                val likes = currentLikes.copy(userLikes = userLikes, count = count)
                it.copy(likes = likes)
            }
        }
    }

    override fun share(postId: Long) {
        data.value = posts.map { if (it.id != postId) it else it.copy(reposts = it.reposts + 1) }
    }

    override fun delete(postId: Long) {
        data.value = posts.filter { it.id != postId }
    }

    override fun save(post: Post) {
        if (post.id == PostRepository.NEW_POST_ID) insert(post) else update(post)
    }

    private fun insert(post: Post) {
        data.value = listOf(
            post.copy(id = ++nextId)
        ) + posts
    }

    private fun update(post: Post) {
        data.value = posts.map {
            if (it.id == post.id) post else it
        }
    }


    private companion object {
        const val GENERATED_POSTS_AMOUNT = 10
    }
}
