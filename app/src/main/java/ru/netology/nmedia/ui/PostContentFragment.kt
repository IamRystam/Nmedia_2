package ru.netology.nmedia.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.Post
import ru.netology.nmedia.databinding.FragmentPostContentBinding
import ru.netology.nmedia.viewModel.PostViewModel

class PostContentFragment : Fragment() {

    private val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= FragmentPostContentBinding.inflate(layoutInflater, container, false).also { binding ->

        val editText = this.arguments?.textArg

        binding.edit.setText(editText)
        binding.edit.requestFocus()
        binding.ok.setOnClickListener {
            val text = binding.edit.text

            if (!text.isNullOrBlank()) {
                viewModel.onSaveButtonClicked(text.toString())
            }
            findNavController().navigateUp()
        }


    }.root

    companion object{
        const val REQUEST_KEY = "requestKey"
        const val RESULT_KEY = "postForSaveContent"
        private const val TEXT_KEY ="TEXT_KEY"
        var Bundle.textArg: String?
            set(value) = putString(TEXT_KEY, value)
            get() = getString(TEXT_KEY)
        private const val POST_ID_KEY ="POST_ID_KEY"
        var Bundle.postId: Long
            set(value) = putLong(POST_ID_KEY, value)
            get() = getLong(POST_ID_KEY)
    }


}
