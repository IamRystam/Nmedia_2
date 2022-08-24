package ru.netology.nmedia.data.impl

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.Post
import ru.netology.nmedia.data.PostRepository
import kotlin.properties.Delegates

class FilePostRepository(
    private val application: Application
) : PostRepository {

    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type

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
            application.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
                .bufferedWriter().use {
                    it.write(gson.toJson(value))
                }
            data.value = value
        }

    override val data: MutableLiveData<List<Post>>

    init {
        val postsFile = application.filesDir.resolve(FILE_NAME)
        val posts: List<Post> = if(postsFile.exists()){
            val inputStream = application.openFileInput(FILE_NAME)
            val reader = inputStream.bufferedReader()
            reader.use {gson.fromJson(it, type) }
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
        const val NEXT_ID_PREFS_KEY = "nextId"
        const val FILE_NAME = "posts.json"
    }
}
