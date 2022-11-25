package ru.netology.firstask.application

import android.app.Application
import ru.netology.firstask.sharedPreferences.AppAuth

class NMediaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
}