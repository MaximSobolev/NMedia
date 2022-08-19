package ru.netology.firstask.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.firstask.databinding.ActivityNewPostBinding


class NewPostActivity : AppCompatActivity() {
    private var binding : ActivityNewPostBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setupListeners()
        setupData()
    }

    private fun initBinding() {
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun setupListeners() {
        binding?.apply {
            addPost.setOnClickListener {
                val intent = Intent()
                if (addContent.text.isBlank()) {
                    setResult(Activity.RESULT_CANCELED)
                } else {
                    val content = addContent.text.toString()
                    intent.putExtra(Intent.EXTRA_TEXT, content)
                    setResult(Activity.RESULT_OK, intent)
                }
                finish()
            }
        }
    }
    private fun setupData () {
        binding?.apply {
            intent.let {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (text != null) addContent.setText(text)
            }
        }
    }

}