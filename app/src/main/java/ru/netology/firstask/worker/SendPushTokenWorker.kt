package ru.netology.firstask.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await
import ru.netology.firstask.dto.PushToken
import ru.netology.firstask.retrofit.PostApi

class SendPushTokenWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
    }
    override suspend fun doWork(): Result =
        try {
            val pushToken = PushToken(
                inputData.getString(TOKEN_KEY) ?: Firebase.messaging.token.await())
            PostApi.userService.saveToken(pushToken)
            Result.success()
        } catch (e : Exception) {
            Result.retry()
        }

}