package ru.netology.firstask.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.firstask.R
import ru.netology.firstask.databinding.FragmentSignInBinding
import ru.netology.firstask.util.AndroidUtils
import ru.netology.firstask.viewmodel.SignInViewModel

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private var binding: FragmentSignInBinding? = null
    private val signInViewModel : SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        setupListeners()
        setupObservers()
        return binding?.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )
    }
    private fun setupListeners() {
        binding?.apply {
            signInButton.setOnClickListener{ view ->
                if (!userLogin.text.isNullOrBlank() && !userPassword.text.isNullOrBlank()) {
                    signInViewModel.signIn(
                        userLogin.text.toString(),
                        userPassword.text.toString())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            R.string.empty_login_or_password,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                AndroidUtils.hideKeyboard(view)
            }
            signUpButton.setOnClickListener{
                findNavController().navigate(R.id.signInFragmentToSignUpFragment)
            }
        }
    }
    private fun setupObservers() {
        signInViewModel.state.observe(viewLifecycleOwner){
            binding?.apply {
                progressBar.isVisible = it.loading
                if (it.idle) {
                    findNavController().navigateUp()
                }
                if (it.error) {
                   when (signInViewModel.errorMessage.value) {
                       R.string.login_or_password_are_wrong -> {
                           Snackbar.make(
                               root,
                               getString(R.string.login_or_password_are_wrong),
                               Snackbar.LENGTH_SHORT)
                               .show()
                       }
                       R.string.retry_text -> {
                           Snackbar.make(root, getString(R.string.retry_text), Snackbar.LENGTH_SHORT)
                               .show()
                       }
                       R.string.network_problems -> {
                           Snackbar.make(root, getString(R.string.network_problems), Snackbar.LENGTH_SHORT)
                               .show()
                       }
                       R.string.unknown_problems -> {
                           Snackbar.make(root, getString(R.string.unknown_problems), Snackbar.LENGTH_SHORT)
                               .show()
                       }
                   }
                }
            }
        }
    }
}
