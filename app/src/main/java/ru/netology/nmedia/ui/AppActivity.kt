package ru.netology.nmedia.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.AppActivityBinding
import ru.netology.nmedia.ui.PostContentFragment.Companion.textArg

class AppActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AppActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent?: return
        if(intent.action != Intent.ACTION_SEND) return
        var text = intent.getStringExtra(Intent.EXTRA_TEXT)
        if(text.isNullOrBlank()) text = "@string/empty_string"

        Snackbar.make(binding.root, text, Snackbar.LENGTH_INDEFINITE)
            .setAction(android.R.string.ok){finish()}
            .show()

        findNavController(R.id.fragmentContainer).navigate(
            R.id.action_feedFragment_to_postContentFragment,
            Bundle().apply {
                textArg = text
            })

    }

}
