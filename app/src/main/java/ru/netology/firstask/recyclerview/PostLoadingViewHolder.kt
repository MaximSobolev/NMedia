package ru.netology.firstask.recyclerview

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import ru.netology.firstask.databinding.PostLoadingItemBinding

class PostLoadingViewHolder(
    private val binding : PostLoadingItemBinding,
    private val retryListener : () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState : LoadState) {
        binding.apply {
            postLoadingProgress.isVisible = loadState is LoadState.Loading
            postLoadingRetry.isVisible = loadState is LoadState.Error
            postLoadingRetry.setOnClickListener {
                retryListener()
            }
        }
    }

}