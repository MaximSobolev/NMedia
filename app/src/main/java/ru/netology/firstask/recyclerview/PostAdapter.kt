package ru.netology.firstask.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.firstask.R
import ru.netology.firstask.databinding.CardAdBinding
import ru.netology.firstask.databinding.CardPostBinding
import ru.netology.firstask.dto.Ad
import ru.netology.firstask.dto.FeedItem
import ru.netology.firstask.dto.Post
import ru.netology.firstask.viewmodel.PostViewModel


class PostAdapter(
    private val viewModel : PostViewModel,
    private val onInteractionListener: OnInteractionListener

    ) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallBack()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, viewModel, onInteractionListener)
            }
            else -> error("unknown view type: $viewType")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }
}
