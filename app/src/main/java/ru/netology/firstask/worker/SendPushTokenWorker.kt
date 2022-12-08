package ru.netology.firstask.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import ru.netology.firstask.dto.PushToken
import ru.netology.firstask.hilt.AppAuthEntryPoint

class SendPushTokenWorker(
    @ApplicationContext
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
    }

    override suspend fun doWork(): Result =
        try {
            val pushToken = PushToken(
                inputData.getString(TOKEN_KEY) ?: Firebase.messaging.token.await())
            val entryPoint =
                EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
            entryPoint.getUserService().saveToken(pushToken)
            Result.success()
        } catch (e : Exception) {
            Result.retry()
        }

}