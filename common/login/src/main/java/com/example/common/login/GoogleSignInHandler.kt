package com.example.common.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException

class GoogleSignInHandler(private val context: Context) {

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestProfile()
        .build()

    private val client: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    fun signInIntent(): Intent = client.signInIntent

    fun currentAccount(): GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)

    fun signOut() = client.signOut()

    class SignInContract : ActivityResultContract<Unit, GoogleSignInAccount?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()
            val client = GoogleSignIn.getClient(context, gso)
            return client.signInIntent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): GoogleSignInAccount? {
            if (resultCode != Activity.RESULT_OK || intent == null) return null
            return try {
                GoogleSignIn.getSignedInAccountFromIntent(intent).getResult(ApiException::class.java)
            } catch (e: ApiException) {
                null
            }
        }
    }
}
