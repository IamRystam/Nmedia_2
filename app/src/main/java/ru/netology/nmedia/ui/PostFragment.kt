package ru.netology.nmedia.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostFragmentBinding
import ru.netology.nmedia.formatCount
import ru.netology.nmedia.ui.PostContentFragment.Companion.postId
import ru.netology.nmedia.ui.PostContentFragment.Companion.textArg
import ru.netology.nmedia.viewModel.PostViewModel


class PostFragment : Fragment() {
    private val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PostFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        val postId = arguments?.postId
        val post = viewModel.data.value?.find {
            it.id == postId
        }

        val popupMenu by lazy {
            PopupMenu(context, binding.postLayout.options).apply {
                inflate(R.menu.options_post)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.remove -> {
                            if (post != null) {
                                viewModel.onDeleteClicked(post)
                                findNavController().navigateUp()
                            }
                            true
                        }
                        R.id.edit -> {
                            if (post != null) {
                                viewModel.onEditClicked(post)
                            }
                            true
                        }
                        else -> false
                    }

                }
            }
        }


        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val currentPost = posts.find {
                it.id == postId
            }
            with(binding.postLayout) {
                if (currentPost != null) {

                    authorName.text = currentPost.author
                    postDate.text =
                        android.text.format.DateFormat.format("yyyy-MM-dd hh:mm", currentPost.date)
                    content.text = currentPost.text
                    likeButton.text = formatCount(currentPost.likes.count)
                    looksCounter.text = formatCount(currentPost.views)
                    shareButton.text = formatCount(currentPost.reposts)
                    likeButton.isChecked = currentPost.likes.userLikes
                    options.setOnClickListener { popupMenu.show() }
                    contentLink.isGone = true
                    videoLink.setImageResource(R.drawable.videobackground)
                    videoPreview.visibility = View.VISIBLE
                    likeButton.setOnClickListener { viewModel.onLikeClicked(currentPost) }
                    shareButton.setOnClickListener { viewModel.onShareClicked(currentPost) }
                    buttonPlay.setOnClickListener { viewModel.onPlayClicked(currentPost) }
                    videoLink.setOnClickListener { viewModel.onPlayClicked(currentPost) }

                }

            }
        }

        viewModel.sharePostContent.observe(viewLifecycleOwner) { postContent ->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, postContent)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
            startActivity(shareIntent)
        }
        viewModel.playVideoLink.observe(viewLifecycleOwner) { videoLink ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoLink))
            startActivity(intent)
        }

        setFragmentResultListener(
            requestKey = PostContentFragment.REQUEST_KEY
        ) { requestKey, bundle ->
            if (requestKey != PostContentFragment.REQUEST_KEY) return@setFragmentResultListener
            val newPostContent = bundle.getString(
                PostContentFragment.RESULT_KEY
            ) ?: return@setFragmentResultListener
            viewModel.onSaveButtonClicked(newPostContent)
        }

        viewModel.navigateToPostContentScreenEvent.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_postFragment_to_postContentFragment)
        }

        viewModel.navigateToEditPostContentScreenEvent.observe(viewLifecycleOwner) {

            findNavController().navigate(
                R.id.action_postFragment_to_postContentFragment,
                Bundle().apply {
                    textArg = it
                })
        }


    }.root

}
