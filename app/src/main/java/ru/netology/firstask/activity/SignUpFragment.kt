package ru.netology.firstask.activity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.firstask.R
import ru.netology.firstask.databinding.FragmentSignUpBinding
import ru.netology.firstask.util.AndroidUtils
import ru.netology.firstask.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {
    private var binding: FragmentSignUpBinding? = null
    private val signUpViewModel : SignUpViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(
                    requireContext(),
                    R.string.error_photo,
                    Toast.LENGTH_SHORT
                ).show()
            }
            Activity.RESULT_OK -> {
                val uri = it.data?.data
                signUpViewModel.savePhoto(uri, uri?.toFile())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        setupObservers()
        setupListeners()
        return binding?.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupObservers() {
        binding?.apply {
            signUpViewModel.photo.observe(viewLifecycleOwner) { photo ->
                avatarContainer.isVisible = photo?.uri != null
                userAvatar.setImageURI(photo?.uri)
            }
            signUpViewModel.state.observe(viewLifecycleOwner) { state ->
                if (state.idle) {
                    findNavController().navigateUp()
                }
                progressBar.isVisible = state.loading
                if (state.error) {
                    when (signUpViewModel.errorMessage.value) {
                        R.string.login_or_password_are_wrong -> {
                            Snackbar.make(
                                root,
                                getString(R.string.login_or_password_are_wrong),
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                        R.string.retry_text -> {
                            Snackbar.make(
                                root,
                                getString(R.string.retry_text),
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                        R.string.network_problems -> {
                            Snackbar.make(
                                root,
                                getString(R.string.network_problems),
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                        R.string.unknown_problems -> {
                            Snackbar.make(
                                root,
                                getString(R.string.unknown_problems),
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }
    }
    private fun setupListeners() {
        binding?.apply {
            addAvatar.setOnClickListener{
                AndroidUtils.hideKeyboard(it)
                ImagePicker.Builder(requireActivity())
                    .crop()
                    .galleryOnly()
                    .maxResultSize(2048, 2048)
                    .createIntent (imageLauncher::launch)
            }
            signUpButton.setOnClickListener{
                if (!userName.text.isNullOrBlank() && !userLogin.text.isNullOrBlank()
                    && !userPassword.text.isNullOrBlank() && !repeatPassword.text.isNullOrBlank()) {
                    when (passComparison()) {
                        true -> {
                            signUpViewModel.signUp(
                                userName.text.toString(),
                                userLogin.text.toString(),
                                userPassword.text.toString())
                        }
                        false -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.passwords_didnt_match,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.input_fields_empty,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                AndroidUtils.hideKeyboard(it)
            }
            deleteAvatar.setOnClickListener {
                signUpViewModel.savePhoto(null, null)
            }
        }
    }

    private fun passComparison() : Boolean =
        when (binding?.userPassword?.text.toString() == binding?.repeatPassword?.text.toString()) {
            true -> true
            false -> false
        }

}