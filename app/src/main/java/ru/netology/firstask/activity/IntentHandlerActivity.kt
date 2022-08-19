package ru.netology.firstask.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import ru.netology.firstask.R
import ru.netology.firstask.contract.NewPostIntentContract
import ru.netology.firstask.databinding.ActivityIntentHandlerBinding
import ru.netology.firstask.viewmodel.PostViewModel

class IntentHandlerActivity : AppCompatActivity() {
    private val viewModel: PostViewModel by viewModels()

    private val newPostLauncher = registerForActivityResult(NewPostIntentContract()) { text ->
        if (text.isNullOrBlank()) {
            Toast.makeText(
                this@IntentHandlerActivity,
                R.string.error_empty_text,
                Toast.LENGTH_SHORT
            ).show()
            return@registerForActivityResult
        } else {
            viewModel.changeContent(text)
            viewModel.save()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntentHandlerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, R.string.error_empty_text, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
            }
            newPostLauncher.launch(text)
        }
    }
}