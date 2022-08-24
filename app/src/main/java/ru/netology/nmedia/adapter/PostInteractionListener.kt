package ru.netology.nmedia.adapter

import ru.netology.nmedia.Post

interface PostInteractionListener {
    fun onLikeClicked(post: Post)
    fun onShareClicked(post: Post)
    fun onDeleteClicked(post: Post)
    fun onEditClicked(post: Post)
    fun onPlayClicked(post: Post)
}
