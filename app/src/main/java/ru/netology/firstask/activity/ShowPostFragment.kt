package ru.netology.firstask.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.map
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.firstask.R
import ru.netology.firstask.databinding.FragmentShowPostBinding
import ru.netology.firstask.dto.Post
import ru.netology.firstask.util.PostArg
import ru.netology.firstask.util.StringArg
import ru.netology.firstask.viewmodel.PostViewModel

private const val BASE_URL = "http://10.0.2.2:9999"

@AndroidEntryPoint
class ShowPostFragment : Fragment() {
    private var binding : FragmentShowPostBinding? = null
    private val viewModel: PostViewModel by activityViewModels()
    private lateinit var swipeRefreshFragment: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        initBinding(inflater, container)
        initSwipeRefresh()
        setupArgument()
        setupObserve()
        setupListeners()
        return binding?.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentShowPostBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun initSwipeRefresh() {
        binding?.apply {
            swipeRefreshFragment = swiperefresh
        }
    }

    private fun setupArgument() {
        arguments?.postArg?.let { postArg ->
            binding?.apply {
                author.text = postArg.author
                Glide.with(avatar)
                    .load("${BASE_URL}/avatars/${postArg.authorAvatar}")
                    .placeholder(R.drawable.ic_baseline_downloading_24)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .timeout(10_000)
                    .into(avatar)
                published.text = postArg.published
                content.text = postArg.content
                like.text = viewModel.largeNumberDisplay(postArg.likes)
                share.text = viewModel.largeNumberDisplay(postArg.share)
                viewCount.text = viewModel.largeNumberDisplay(postArg.view)
                like.isChecked = postArg.likedByMe
                if (postArg.attachment == null) {
                    videoPreview.visibility = View.GONE
                } else {
                    Glide.with(videoPreview)
                        .load("${BASE_URL}/media/${postArg.attachment?.url}")
                        .placeholder(R.drawable.ic_baseline_downloading_24)
                        .error(R.drawable.ic_baseline_error_outline_24)
                        .timeout(10_000)
                        .into(videoPreview)
                    videoPreview.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupObserve() {
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest { state ->
                arguments?.postArg?.let { postArg ->
                    updatePost(state, postArg)
                }
            }
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding?.apply {
                if (state.error) {
                    Snackbar.make(root, R.string.retry_text, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.retry) { viewModel.loadPosts() }
                        .show()
                }
                swipeRefreshFragment.isRefreshing = state.refreshing
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            binding?.apply {
                Snackbar.make(root, getString(message), Snackbar.LENGTH_SHORT)
                    .setAction(R.string.retry) {viewModel.retryOperation()}
                    .show()
            }
        }
    }

    private fun setupListeners() {
        arguments?.postArg?.let { postArg ->
            binding?.apply {
                like.setOnClickListener{
                    viewModel.likeById(postArg)
                }
                share.setOnClickListener{
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, postArg.content)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(intent, getString(R.string.share_post))
                    startActivity(shareIntent)
                    viewModel.shareById(postArg.id)
                }
                moreButton.setOnClickListener{view ->
                    PopupMenu(view.context, view).apply {
                        inflate(R.menu.options_post)
                        setOnMenuItemClickListener { item ->
                            when(item.itemId) {
                                R.id.remove -> {
                                    viewModel.removeById(postArg.id)
                                    findNavController().navigate(R.id.showPostFragmentToFeedFragment)
                                    true
                                }
                                R.id.edit -> {
                                    findNavController().navigate(R.id.showPostFragmentToNewPostFragment,
                                        Bundle().apply {
                                            textArg = postArg.content
                                        })
                                    viewModel.edit(postArg)
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }
                videoPreview.setOnClickListener {
                    findNavController().navigate(R.id.showPostFragmentToShowPhotoFragment,
                        Bundle().apply
                        { textArg = postArg.attachment?.url })
                }

            }
        }
        swipeRefreshFragment.setOnRefreshListener {
            viewModel.refreshPosts()
        }
    }

    private fun updatePost(state : PagingData<Post>?, oldPost : Post?) {
        val posts = ArrayList<Post>()
        state?.map { posts.add(it) }
        val post = posts.find { it.id == oldPost?.id }
        post?.let {
            binding?.apply {
                content.text = post.content
                like.text = viewModel.largeNumberDisplay(post.likes)
                share.text = viewModel.largeNumberDisplay(post.share)
            }
        }
    }

    companion object {
        var Bundle.textArg :String? by StringArg
        var Bundle.postArg : Post? by PostArg
    }
}




