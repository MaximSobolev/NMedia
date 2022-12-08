package ru.netology.firstask.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.netology.firstask.sharedPreferences.AppAuth

@HiltAndroidApp
class NMediaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
}