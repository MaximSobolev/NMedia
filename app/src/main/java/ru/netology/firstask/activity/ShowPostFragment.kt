package ru.netology.firstask.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.firstask.R
import ru.netology.firstask.databinding.CardPostBinding
import ru.netology.firstask.dto.Post
import ru.netology.firstask.util.PostArg
import ru.netology.firstask.util.StringArg
import ru.netology.firstask.viewmodel.PostViewModel



class ShowPostFragment : Fragment() {
    private var binding : CardPostBinding? = null
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        initBinding(inflater, container)
        setupArgument()
        setupObserve()
        setupListeners()
        return binding?.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = CardPostBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupArgument() {
        arguments?.postArg?.let { postArg ->
            binding?.apply {
                author.text = postArg.author
                published.text = postArg.published
                content.text = postArg.content
                like.text = viewModel.largeNumberDisplay(postArg.like)
                share.text = viewModel.largeNumberDisplay(postArg.share)
                viewCount.text = viewModel.largeNumberDisplay(postArg.view)
                like.isChecked = postArg.likeByMe
                if (viewModel.showPreviewVideo(postArg)) {
                    videoName.text = postArg.videoName
                    videoViewCount.text = "${viewModel.largeNumberDisplay(postArg.videoViewCount ?: 0)} views"
                    videoGroup.visibility = View.VISIBLE
                } else {
                    videoGroup.visibility = View.GONE
                }
            }
        }
    }

    private fun setupObserve() {
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            arguments?.postArg?.let { postArg ->
                binding?.apply {
                    val post = posts.find { postArg.id == it.id }
                    post?.let {
                        content.text = post.content
                        like.text = viewModel.largeNumberDisplay(post.like)
                        share.text = viewModel.largeNumberDisplay(post.share)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        arguments?.postArg?.let { postArg ->
            binding?.apply {
                like.setOnClickListener{
                    viewModel.likeById(postArg.id)
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
            }
        }
    }

    companion object {
        var Bundle.textArg :String? by StringArg
        var Bundle.postArg : Post? by PostArg
    }
}




