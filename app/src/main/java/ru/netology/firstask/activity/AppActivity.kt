package ru.netology.firstask.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import ru.netology.firstask.R
import ru.netology.firstask.activity.NewPostFragment.Companion.draftPostArg
import ru.netology.firstask.dto.DraftPost
import ru.netology.firstask.sharedPreferences.AppAuth
import ru.netology.firstask.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModels<AuthViewModel>()
        var currentMenuProvider : MenuProvider? = null
        viewModel.data.observe(this) {
            currentMenuProvider?.let(::removeMenuProvider)
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.options_authentication, menu)
                    menu.let {
                        it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
                        it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.signIn -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.signInFragment)
                            true
                        }
                        R.id.signUp -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.signUpFragment)
                            true
                        }
                        R.id.signOut -> {
                            if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.newPostFragment) {
                                findViewById<View>(R.id.dialogWindow).visibility = View.VISIBLE
                                true
                            } else {
                                AppAuth.getInstance().removeAuth()
                                true
                            }
                        }
                            else -> false
                        }
                }.also {
                currentMenuProvider = it
                }
            )
        }

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.feedFragmentToNewPostFragment,
                Bundle().apply {
                    draftPostArg = DraftPost(text, null)
                }
            )
        }
    }
}