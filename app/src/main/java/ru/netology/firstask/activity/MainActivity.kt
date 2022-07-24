package ru.netology.firstask.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.firstask.R
import ru.netology.firstask.databinding.ActivityMainBinding
import ru.netology.firstask.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBinding()
        subscribe()
        setupListeners()
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun subscribe() {
        viewModel.data.observe(this) { post ->
            binding?.apply {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likeCount.text = viewModel.largeNumberDisplay(post.like)
                shareCount.text = viewModel.largeNumberDisplay(post.share)
                viewCount.text = viewModel.largeNumberDisplay(post.view)
                like.setImageResource(
                    if (post.likeByMe) ru.netology.firstask.R.drawable.ic_baseline_favorite_24 else
                        ru.netology.firstask.R.drawable.ic_baseline_favorite_border_24
                )
            }
        }
    }

    private fun setupListeners() {
        viewModel.data.observe(this) {
            binding?.apply {
                like.setOnClickListener {
                    viewModel.like()
                }
                share.setOnClickListener {
                    viewModel.share()
                }
            }
        }
    }
}