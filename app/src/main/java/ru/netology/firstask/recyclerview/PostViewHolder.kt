package ru.netology.firstask.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.netology.firstask.databinding.CardPostBinding
import ru.netology.firstask.dto.Post
import ru.netology.firstask.viewmodel.PostViewModel

class PostViewHolder(
    private val binding: CardPostBinding,
    private val viewModel : PostViewModel,
    private val onLikeListener: OnLikeListener,
    private val onShareListener : OnShareListener
) : RecyclerView.ViewHolder(binding.root) {

//    private var itemPost: Post? = null
//    private val likeOnClickListener: View.OnClickListener =
//        View.OnClickListener {
//            itemPost?.let { onLikeListener(it) }
//        }
//    private val shareOnClickListener: View.OnClickListener =
//        View.OnClickListener {
//            itemPost?.let { onShareListener(it) }
//        }
//
//    init {
//        binding.like.setOnClickListener(likeOnClickListener)
//        binding.share.setOnClickListener(shareOnClickListener)
//    }

    fun bind(post: Post) {
        binding.apply {
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
//            itemPost = post
            like.setOnClickListener {
                onLikeListener(post)
            }
            share.setOnClickListener {
                onShareListener(post)
            }
        }
    }
}