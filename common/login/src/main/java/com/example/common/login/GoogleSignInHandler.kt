package com.example.common.login

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "GoogleSignInHandler"

class GoogleSignInHandler(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val onSignInSuccess: (GoogleIdTokenCredential) -> Unit,
    private val onSignInFailure: (Exception) -> Unit
) {

    private val credentialManager = CredentialManager.create(context)
    private val webClientId: String = context.getString(R.string.google_web_client_id_modern)


    private fun buildGoogleIdOption(filterByAuthorizedAccounts: Boolean, nonce: String? = null): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .setNonce(nonce ?: generateNonce())
            .build()
    }

    private fun buildSignInWithGoogleOption(nonce: String? = null): GetSignInWithGoogleOption {
        return GetSignInWithGoogleOption.Builder(webClientId)
            .setNonce(nonce ?: generateNonce())
            .build()
    }

    private fun generateNonce(): String {
        return java.util.UUID.randomUUID().toString()
    }

    fun signIn() {
        fetchCredentials(filterByAuthorizedAccounts = true)
    }

    fun signInWithGoogleButton() {
        val signInWithGoogleOption = buildSignInWithGoogleOption()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()
        fetchCredentialsInternal(request, isSignUp = false)
    }


    private fun fetchCredentials(filterByAuthorizedAccounts: Boolean, isSignUpAttempt: Boolean = false) {
        val googleIdOption = buildGoogleIdOption(filterByAuthorizedAccounts = filterByAuthorizedAccounts)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        fetchCredentialsInternal(request, isSignUp = !filterByAuthorizedAccounts || isSignUpAttempt)
    }

    private fun fetchCredentialsInternal(request: GetCredentialRequest, isSignUp: Boolean) {
        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context as Activity,
                )
                handleSignInResult(result)
            } catch (e: GetCredentialException) {
                Log.e(TAG, "GetCredentialException: ${e.javaClass.simpleName}, ${e.message}", e)

                val wasSignInAttemptWithAuthorizedAccounts = request.credentialOptions.any {
                    it is GetGoogleIdOption && it.filterByAuthorizedAccounts
                }
                if (wasSignInAttemptWithAuthorizedAccounts && !isSignUp) {
                    Log.d(TAG, "No authorized accounts found or sign-in failed, attempting sign-up.")
                    fetchCredentials(filterByAuthorizedAccounts = false, isSignUpAttempt = true)
                } else {
                    onSignInFailure(e)
                }
            }
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        Log.d(TAG, "Google ID Token received: ID - ${googleIdTokenCredential.id}, Display Name - ${googleIdTokenCredential.displayName}")
                        onSignInSuccess(googleIdTokenCredential)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                        onSignInFailure(e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of custom credential: ${credential.type}")
                    onSignInFailure(RuntimeException("Unexpected type of custom credential: ${credential.type}"))
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential: ${credential.javaClass.name}")
                onSignInFailure(RuntimeException("Unexpected type of credential: ${credential.javaClass.name}"))
            }
        }
    }

    fun signOut() {
        coroutineScope.launch {
            try {
                credentialManager.clearCredentialState(
                    androidx.credentials.ClearCredentialStateRequest()
                )
                Log.d(TAG, "Credential state cleared successfully.")
            } catch (e: androidx.credentials.exceptions.ClearCredentialException) {
                Log.e(TAG, "Error clearing credential state", e)
                onSignInFailure(e)
            }
        }
    }
}
