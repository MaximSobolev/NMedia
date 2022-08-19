package ru.netology.firstask.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.firstask.R
import ru.netology.firstask.contract.NewPostIntentContract
import ru.netology.firstask.databinding.ActivityMainBinding
import ru.netology.firstask.dto.Post
import ru.netology.firstask.recyclerview.OnInteractionListener
import ru.netology.firstask.recyclerview.PostAdapter
import ru.netology.firstask.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    private val viewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostAdapter
    private var newPost = false

    private val newPostLauncher = registerForActivityResult(NewPostIntentContract()) { text ->
        if (text.isNullOrBlank()) {
                Toast.makeText(
                    this@MainActivity,
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
        initBinding()
        initAdapter()
        setupObserve()
        setupListeners()
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun initAdapter() {
        adapter = PostAdapter(
            viewModel,
            object : OnInteractionListener {
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(intent, getString(R.string.share_post))
                    startActivity(shareIntent)
                    viewModel.shareById(post.id)
                }

                override fun onEdit(post: Post) {
                    newPostLauncher.launch(post.content)
                    viewModel.edit(post)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun showVideo(post: Post) {
                    val url = post.videoUrl
                    val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(videoIntent)
                }

            })
        binding?.list?.adapter = adapter
    }

    private fun setupObserve() {
        viewModel.data.observe(this) { posts ->
            newPost = posts.size > adapter.itemCount
            adapter.submitList(posts) {
                if (newPost) {
                    binding?.list?.scrollToPosition(0)
                }
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            addPost.setOnClickListener{
                newPostLauncher.launch(null)
            }
        }
    }
}