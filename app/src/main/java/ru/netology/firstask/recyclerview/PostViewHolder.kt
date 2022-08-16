package ru.netology.firstask.recyclerview

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.firstask.R
import ru.netology.firstask.databinding.CardPostBinding
import ru.netology.firstask.dto.Post
import ru.netology.firstask.viewmodel.PostViewModel

class PostViewHolder(
    private val binding: CardPostBinding,
    private val viewModel : PostViewModel,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    private var itemPost: Post? = null
    private val likeOnClickListener: View.OnClickListener =
        View.OnClickListener {
            itemPost?.let { onInteractionListener.onLike(it) }
        }
    private val shareOnClickListener: View.OnClickListener =
        View.OnClickListener {
            itemPost?.let { onInteractionListener.onShare(it) }
        }
    private val menuOnClickListener : View.OnClickListener =
        View.OnClickListener { view ->
            itemPost?.let { post ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when(item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }

    init {
        binding.like.setOnClickListener(likeOnClickListener)
        binding.share.setOnClickListener(shareOnClickListener)
        binding.moreButton.setOnClickListener(menuOnClickListener)
    }

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.setText(viewModel.largeNumberDisplay(post.like))
            share.setText(viewModel.largeNumberDisplay(post.share))
            viewCount.text = viewModel.largeNumberDisplay(post.view)
            like.isChecked = post.likeByMe
            itemPost = post
        }
    }
}