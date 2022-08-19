package ru.netology.firstask.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.firstask.activity.NewPostActivity

//class NewPostIntentContract : ActivityResultContract<Unit, String?>() {
//    override fun createIntent(context: Context, input: Unit): Intent =
//        Intent(context,NewPostActivity::class.java)
//
//    override fun parseResult(resultCode: Int, intent: Intent?): String? =
//        if (resultCode == Activity.RESULT_OK) {
//            intent?.getStringExtra(Intent.EXTRA_TEXT)
//        } else {
//            null
//        }
//}
class NewPostIntentContract : ActivityResultContract<String?, String?>() {
    override fun createIntent(context: Context, input: String?): Intent =
        when(input) {
            null -> Intent(context,NewPostActivity::class.java)
            else -> {
                val intent = Intent (context, NewPostActivity::class.java)
                intent.putExtra(Intent.EXTRA_TEXT, input)
                intent
            }
        }

    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }
}