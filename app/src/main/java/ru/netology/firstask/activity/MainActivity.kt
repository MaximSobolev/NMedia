package ru.netology.firstask.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.firstask.databinding.ActivityMainBinding
import ru.netology.firstask.recyclerview.PostAdapter
import ru.netology.firstask.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    private val viewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initAdapter()
        setupAdapter()
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun initAdapter() {
        adapter = PostAdapter(
            viewModel,
            { viewModel.likeById(it.id) },
            { viewModel.shareById(it.id) })
        binding?.list?.adapter = adapter
    }

    private fun setupAdapter() {
        viewModel.data.observe(this) { posts ->
                adapter.submitList(posts)
        }
    }
}