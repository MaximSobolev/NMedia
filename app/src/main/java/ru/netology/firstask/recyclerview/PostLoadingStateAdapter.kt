package ru.netology.firstask.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import ru.netology.firstask.databinding.PostLoadingItemBinding

class PostLoadingStateAdapter (
    private val retryListener : () -> Unit
        ) : LoadStateAdapter<PostLoadingViewHolder>() {

    override fun onBindViewHolder(holder: PostLoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PostLoadingViewHolder =
        PostLoadingViewHolder(
            PostLoadingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            retryListener
        )
}