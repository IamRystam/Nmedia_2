package ru.netology.nmedia

import kotlinx.serialization.Serializable
import kotlinx.datetime.serializers.LocalDateComponentSerializer
import java.time.LocalDate

@Serializable
data class Post(
    val id: Long,
    val text: String,
    val author: String,
    val date: Long,
    val likes: Likes,
    val views: Int,
    val reposts: Int,
    val video: String?
)
@Serializable
data class Likes(
    val count: Int,
    val userLikes: Boolean
)
