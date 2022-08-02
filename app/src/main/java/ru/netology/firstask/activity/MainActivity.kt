package ru.netology.firstask.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.firstask.R
import ru.netology.firstask.databinding.ActivityMainBinding
import ru.netology.firstask.dto.Post
import ru.netology.firstask.recyclerview.OnInteractionListener
import ru.netology.firstask.recyclerview.PostAdapter
import ru.netology.firstask.util.AndroidUtils
import ru.netology.firstask.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    private val viewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostAdapter
    private lateinit var text : String
    private var newPost = false

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
                    viewModel.shareById(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
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
        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                return@observe
            }
            binding?.apply {
                editContent.setText(post.content)
                editGroup.visibility = View.VISIBLE

                addContent.requestFocus()
                addContent.setText(post.content)
            }

        }
    }

    private fun setupListeners() {
        binding?.apply {
            save.setOnClickListener {
                text = addContent.text.toString()

                if (text.isBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        R.string.error_empty_text,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text)
                viewModel.save()

                editGroup.visibility = View.GONE

                addContent.setText("")
                AndroidUtils.hideKeyboard(it)
                addContent.clearFocus()
            }

            editOut.setOnClickListener {
                viewModel.save()

                editGroup.visibility = View.GONE
                addContent.setText("")
                AndroidUtils.hideKeyboard(it)
                addContent.clearFocus()
            }
        }
    }
}