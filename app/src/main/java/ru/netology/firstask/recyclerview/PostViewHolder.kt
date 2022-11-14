package ru.netology.firstask.recyclerview

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.firstask.R
import ru.netology.firstask.databinding.CardPostBinding
import ru.netology.firstask.dto.Post
import ru.netology.firstask.viewmodel.PostViewModel

class PostViewHolder(
    private val binding: CardPostBinding,
    private val viewModel : PostViewModel,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
    }

    private val options : RequestOptions = RequestOptions()

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

    private val openPostFragmentOnClickListener: View.OnClickListener =
        View.OnClickListener {
            itemPost?.let { onInteractionListener.openPost(it)}
        }

    init {
        binding.like.setOnClickListener(likeOnClickListener)
        binding.share.setOnClickListener(shareOnClickListener)
        binding.moreButton.setOnClickListener(menuOnClickListener)
        binding.cardPostContainer.setOnClickListener(openPostFragmentOnClickListener)
    }

    fun bind(post: Post) {
        binding.apply {
                if (post.displayOnScreen) {
                    cardPostContainer.visibility = View.VISIBLE
                } else {
                    cardPostContainer.visibility = View.GONE
                }
                author.text = post.author
                Glide.with(avatar)
                    .load("${BASE_URL}/avatars/${post.authorAvatar}")
                    .placeholder(R.drawable.ic_baseline_downloading_24)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .timeout(10_000)
                    .apply(options.circleCrop())
                    .into(avatar)
                published.text = post.published
                content.text = post.content
                like.text = viewModel.largeNumberDisplay(post.likes)
                share.text = viewModel.largeNumberDisplay(post.share)
                viewCount.text = viewModel.largeNumberDisplay(post.view)
                like.isChecked = post.likedByMe
                if (post.attachment == null) {
                    videoPreview.visibility = View.GONE
                } else {
                    Glide.with(videoPreview)
                        .load("${BASE_URL}/images/${post.attachment?.url}")
                        .placeholder(R.drawable.ic_baseline_downloading_24)
                        .error(R.drawable.ic_baseline_error_outline_24)
                        .timeout(10_000)
                        .into(videoPreview)
                    videoPreview.visibility = View.VISIBLE
                }
                if (post.uploadedOnServer) {
                    sending.visibility = View.GONE
                    loaded.visibility = View.VISIBLE
                } else {
                    loaded.visibility = View.GONE
                    sending.visibility = View.VISIBLE
                }
                itemPost = post
        }
    }
}