package ru.netology.firstask.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.firstask.R
import ru.netology.firstask.databinding.FragmentNewPostBinding
import ru.netology.firstask.viewmodel.PostViewModel
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.firstask.dto.DraftPost
import ru.netology.firstask.sharedPreferences.AppAuth
import ru.netology.firstask.util.DraftPostArg
import javax.inject.Inject

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private var binding : FragmentNewPostBinding? = null
    private val viewModel: PostViewModel by activityViewModels()

    @Inject
    lateinit var appAuth: AppAuth

    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when(it.resultCode) {
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(
                    requireContext(),
                    R.string.error_photo,
                    Toast.LENGTH_SHORT
                ).show()
            }
            Activity.RESULT_OK -> {
                val uri : Uri? = it.data?.data
                viewModel.savePhoto(uri, uri?.toFile())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        setupMenu()
        setupArguments()
        setupListeners()
        setupObserver()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true){
                @SuppressLint("SuspiciousIndentation")
                override fun handleOnBackPressed() {
                    binding?.apply {
                        if (addContent.text.isNotBlank())
                        viewModel.setDraft(addContent.text.toString())
                    }
                    findNavController().navigate(R.id.newPostFragmentToFeedFragment)
                }
            })
        return binding?.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_save_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                if (menuItem.itemId == R.id.save) {
                    binding?.apply {
                        if (addContent.text.isBlank()) {
                            Toast.makeText(
                                requireContext(),
                                R.string.error_empty_text,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.changeContent(binding?.addContent?.text.toString())
                            viewModel.save()
                            findNavController().navigateUp()
                        }
                    }
                    true
                } else {
                    false
                }

        }, viewLifecycleOwner)
    }

    private fun setupArguments() {
        arguments?.draftPostArg.let {
            binding?.addContent?.setText(it?.content)
            viewModel.savePhoto(it?.photo?.uri, it?.photo?.file)
        }
    }

    private fun setupListeners() {
        binding?.apply {
            takePhoto.setOnClickListener {
                ImagePicker.Builder(requireActivity())
                    .crop()
                    .cameraOnly()
                    .maxResultSize(2048, 2048)
                    .createIntent(imageLauncher::launch)
            }
            downloadPhoto.setOnClickListener {
                ImagePicker.Builder(requireActivity())
                    .crop()
                    .galleryOnly()
                    .maxResultSize(2048, 2048)
                    .createIntent(imageLauncher::launch)
            }
            previewClear.setOnClickListener {
                viewModel.savePhoto(null, null)
            }
            dialogYes.setOnClickListener{
                appAuth.removeAuth()
                findNavController().navigateUp()
            }
            dialogCancel.setOnClickListener{
                dialogWindow.visibility = View.GONE
            }
        }
    }

    private fun setupObserver() {
        binding?.apply {
            viewModel.photo.observe(viewLifecycleOwner) {
                previewContainer.isVisible = it?.uri != null
                previewPhoto.setImageURI(it?.uri)
            }
        }
    }

    companion object {
        var Bundle.draftPostArg : DraftPost? by DraftPostArg
    }
}
