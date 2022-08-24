package ru.netology.nmedia.data.impl

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.netology.nmedia.Post
import ru.netology.nmedia.data.PostRepository
import kotlin.properties.Delegates

class SharedPrefsPostRepository(
    application: Application
) : PostRepository {

    private val prefs = application.getSharedPreferences(
        "repo", Context.MODE_PRIVATE
    )

    private var nextId by Delegates.observable(
        prefs.getLong(NEXT_ID_PREFS_KEY, 0L)
    ){_,_,newValue->
        prefs.edit { putLong(NEXT_ID_PREFS_KEY, newValue) }

    }

    private var posts
        get() = checkNotNull(data.value) {
            "Data value should not be null"
        }
    set(value) {
        prefs.edit {
            val serializedPosts = Json.encodeToString(value)
            putString(POSTS_PREFS_KEY, serializedPosts)
        }
        data.value = value
    }

    override val data: MutableLiveData<List<Post>>

    init {
        val serializedPosts = prefs.getString(POSTS_PREFS_KEY, null)
        val posts: List<Post> = if (serializedPosts!=null){
            Json.decodeFromString(serializedPosts)
        } else emptyList()
        data = MutableLiveData(posts)
    }

    override fun like(postId: Long) {

        posts = posts.map {
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
        posts = posts.map { if (it.id != postId) it else it.copy(reposts = it.reposts + 1) }
    }

    override fun delete(postId: Long) {
        posts = posts.filter { it.id != postId }
    }

    override fun save(post: Post) {
        if (post.id == PostRepository.NEW_POST_ID) insert(post) else update(post)
    }

    private fun insert(post: Post) {
        posts = listOf(
            post.copy(id = ++nextId)
        ) + posts
    }

    private fun update(post: Post) {
        posts = posts.map {
            if (it.id == post.id) post else it
        }
    }


    private companion object {
        const val POSTS_PREFS_KEY = "posts"
        const val NEXT_ID_PREFS_KEY = "nextId"
    }
}
