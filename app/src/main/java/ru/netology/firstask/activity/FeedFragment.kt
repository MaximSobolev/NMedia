package ru.netology.firstask.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.firstask.R
import ru.netology.firstask.databinding.FragmentFeedBinding
import ru.netology.firstask.dto.DraftPost
import ru.netology.firstask.dto.Post
import ru.netology.firstask.recyclerview.OnInteractionListener
import ru.netology.firstask.recyclerview.PostAdapter
import ru.netology.firstask.util.DraftPostArg
import ru.netology.firstask.util.PostArg
import ru.netology.firstask.util.StringArg
import ru.netology.firstask.viewmodel.AuthViewModel
import ru.netology.firstask.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private var binding : FragmentFeedBinding? = null
    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var adapter: PostAdapter
    private lateinit var swipeRefreshFragment : SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        initAdapter()
        initSwipeRefresh()
        setupObserve()
        setupListeners()

        return binding?.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun initAdapter() {
        adapter = PostAdapter(
            viewModel,
            object : OnInteractionListener {
                override fun onLike(post: Post) {
                    binding?.apply {
                        if (authViewModel.authenticated) {
                            viewModel.likeById(post)
                        } else {
                            dialogWindow.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onShare(post: Post) {
                    binding?.apply {
                        if (authViewModel.authenticated) {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, post.content)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(intent, getString(R.string.share_post))
                            startActivity(shareIntent)
                            viewModel.shareById(post.id)
                        } else {
                            dialogWindow.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onEdit(post: Post) {
                    findNavController().navigate(R.id.feedFragmentToNewPostFragment,
                    Bundle().apply {
                        draftPostArg = DraftPost(content = post.content, null)
                    })
                    viewModel.edit(post)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun openPost(post: Post) {
                    findNavController().navigate(R.id.feedFragmentToShowPostFragment,
                    Bundle().apply
                     { postArg = post })
                }

                override fun openPhoto(url: String) {
                    findNavController().navigate(R.id.feedFragmentToShowPhotoFragment,
                        Bundle().apply
                        { textArg = url })
                }

            })
        binding?.list?.adapter = adapter
    }

    private fun initSwipeRefresh() {
        binding?.apply {
            swipeRefreshFragment = swiperefresh
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun setupObserve() {
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest { state ->
                adapter.submitData(state)
            }
        }
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding?.swiperefresh?.isRefreshing = it.refresh is LoadState.Loading
                        || it.append is LoadState.Loading
            }
        }

        viewModel.authState.observe(viewLifecycleOwner) {
            adapter.refresh()
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding?.apply {
                if (state.error) {
                    Snackbar.make(root, R.string.retry_text, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.retry) { adapter.refresh() }
                        .show()
                }
            }
            swipeRefreshFragment.isRefreshing = state.refreshing
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
        binding?.apply {
            addPost.setOnClickListener{
                if (authViewModel.authenticated) {
                    val draft = viewModel.getDraft()
                    if (draft != null) {
                        findNavController().navigate(R.id.feedFragmentToNewPostFragment,
                            Bundle().apply {
                                draftPostArg = draft
                            })
                    } else {
                        findNavController().navigate(R.id.feedFragmentToNewPostFragment)
                    }
                } else {
                    dialogWindow.visibility = View.VISIBLE
                }
            }
            swipeRefreshFragment.setOnRefreshListener {
                adapter.refresh()
            }
            dialogCancel.setOnClickListener{
                dialogWindow.visibility = View.GONE
            }
            dialogSignIn.setOnClickListener{
                findNavController().navigate(R.id.feedFragmentToSignInFragment)
            }
        }
    }

    companion object {
        var Bundle.textArg: String? by StringArg
        var Bundle.postArg : Post? by PostArg
        var Bundle.draftPostArg : DraftPost? by DraftPostArg
    }
}