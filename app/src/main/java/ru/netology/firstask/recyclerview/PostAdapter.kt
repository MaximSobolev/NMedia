package ru.netology.firstask.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.firstask.databinding.CardPostBinding
import ru.netology.firstask.dto.Post
import ru.netology.firstask.viewmodel.PostViewModel


class PostAdapter(
    private val viewModel : PostViewModel,
    private val onInteractionListener: OnInteractionListener

    ) : ListAdapter<Post, PostViewHolder>(PostDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, viewModel, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}
