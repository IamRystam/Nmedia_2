package ru.netology.nmedia.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.ui.PostContentFragment.Companion.textArg
import ru.netology.nmedia.viewModel.PostViewModel

class FeedFragment : Fragment() {
    private val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= FragmentFeedBinding.inflate(layoutInflater, container, false).also { binding ->
        val adapter = PostsAdapter(viewModel)
        binding.postsRecyclerView.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner){posts ->
            adapter.submitList(posts)
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_postContentFragment)
        }

        viewModel.sharePostContent.observe(viewLifecycleOwner){postContent->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, postContent)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
            startActivity(shareIntent)
        }
        viewModel.playVideoLink.observe(viewLifecycleOwner){videoLink->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoLink))
            startActivity(intent)
        }

        setFragmentResultListener(requestKey = PostContentFragment.REQUEST_KEY){
                requestKey, bundle ->
            if (requestKey!= PostContentFragment.REQUEST_KEY) return@setFragmentResultListener
            val newPostContent = bundle.getString(PostContentFragment.RESULT_KEY)?:return@setFragmentResultListener
            viewModel.onSaveButtonClicked(newPostContent)
        }

        viewModel.navigateToPostContentScreenEvent.observe(viewLifecycleOwner){
            findNavController().navigate(R.id.action_feedFragment_to_postContentFragment)
        }

        viewModel.navigateToEditPostContentScreenEvent.observe(viewLifecycleOwner){

            findNavController().navigate(
                R.id.action_feedFragment_to_postContentFragment,
                Bundle().apply {
                    textArg = it
                })
        }

    }.root

}
