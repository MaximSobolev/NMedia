package ru.netology.firstask.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.firstask.R
import ru.netology.firstask.databinding.FragmentNewPostBinding
import ru.netology.firstask.util.StringArg
import ru.netology.firstask.viewmodel.PostViewModel


class NewPostFragment : Fragment() {
    private var binding : FragmentNewPostBinding? = null
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        setupArguments()
        setupListeners()
        return binding?.root
    }
    private fun setupArguments() {
        arguments?.textArg.let {
            binding?.addContent?.setText(it)
        }
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupListeners() {
        binding?.apply {
            addPost.setOnClickListener {
                if (addContent.text.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        R.string.error_empty_text,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    viewModel.changeContent(binding?.addContent?.text.toString())
                    viewModel.save()
                }
                findNavController().navigateUp()
            }
        }
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}