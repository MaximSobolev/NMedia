package ru.netology.firstask.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.firstask.R
import ru.netology.firstask.databinding.FragmentFeedBinding
import ru.netology.firstask.dto.Post
import ru.netology.firstask.recyclerview.OnInteractionListener
import ru.netology.firstask.recyclerview.PostAdapter
import ru.netology.firstask.util.PostArg
import ru.netology.firstask.util.StringArg
import ru.netology.firstask.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    private var binding : FragmentFeedBinding? = null
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private lateinit var adapter: PostAdapter
    private var newPost = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        initAdapter()
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
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(intent, getString(R.string.share_post))
                    startActivity(shareIntent)
                    viewModel.shareById(post.id)
                }

                override fun onEdit(post: Post) {
                    findNavController().navigate(R.id.feedFragmentToNewPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
                    viewModel.edit(post)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun showVideo(post: Post) {
                    val url = post.videoUrl
                    if (url == null) return
                    val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(videoIntent)
                }

                override fun openPost(post: Post) {
                    findNavController().navigate(R.id.feedFragmentToShowPostFragment,
                    Bundle().apply
                     { postArg = post })
                }

            })
        binding?.list?.adapter = adapter
    }

    private fun setupObserve() {
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            newPost = posts.size > adapter.itemCount
            adapter.submitList(posts) {
                if (newPost) {
                    binding?.list?.scrollToPosition(0)
                }
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            addPost.setOnClickListener{
                findNavController().navigate(R.id.feedFragmentToNewPostFragment)
            }
        }
    }
    companion object {
        var Bundle.textArg: String? by StringArg
        var Bundle.postArg : Post? by PostArg
    }
}