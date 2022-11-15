package ru.netology.firstask.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ru.netology.firstask.BuildConfig
import ru.netology.firstask.R
import ru.netology.firstask.databinding.FragmentShowPhotoBinding
import ru.netology.firstask.util.StringArg

private const val BASE_URL = BuildConfig.BASE_URL

class ShowPhotoFragment : Fragment() {
    private var binding : FragmentShowPhotoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        setupArgument()
        setupListeners()
        return binding?.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentShowPhotoBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupArgument() {
        binding?.apply {
            arguments?.textArg?.let {
                    Glide.with(photo)
                        .load("$BASE_URL/media/${it}")
                        .placeholder(R.drawable.ic_baseline_downloading_24)
                        .error(R.drawable.ic_baseline_error_outline_24)
                        .timeout(10_000)
                        .into(photo)
                } ?: Snackbar.make(root, R.string.error_show_photo, Snackbar.LENGTH_SHORT)
                .setAction(R.string.return_back) { findNavController().navigateUp() }
                .show()
            }
    }

    private fun setupListeners() {
        binding?.apply {
            back.setOnClickListener{
                findNavController().navigateUp()
            }
        }
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}