package com.example.pingu.core.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.pingu.core.login.ui.theme.PinguTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configura Google Sign-In
        // Richiedi l'ID token e l'email dell'utente. L'ID token è necessario per autenticarsi
        // con il tuo backend server (se ne hai uno).
        // DEFAULT_SIGN_IN richiede solo l'ID e il profilo di base dell'utente.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("639402548159-qen0drmklegdf0k2oamjtf28ma0v5ai5.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            PinguTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        onGoogleSignInClicked = { signIn() }
                    )
                }
            }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // Launcher per il risultato dell'attività di Google Sign-In
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                // Login fallito o annullato dall'utente
                Log.w("LoginActivity", "Google Sign-In failed or cancelled. Result code: ${result.resultCode}")
                // TODO: Mostra un messaggio di errore all'utente se necessario
            }
        }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            // Login riuscito, ottieni le informazioni dell'account
            val googleIdToken: String? = account.idToken
            Log.d("LoginActivity", "Google Sign-In Succeeded! User: ${account.displayName}, Email: ${account.email}, ID Token: $googleIdToken")

            // TODO: Vai alla schermata successiva o salva l’account
            // Ad esempio, potresti passare l'idToken al tuo backend per la verifica
            // e la creazione di una sessione utente.

        } catch (e: ApiException) {
            // Login fallito
            Log.w("LoginActivity", "signInResult:failed code=" + e.statusCode)
            // TODO: Mostra un messaggio di errore all'utente
            // Puoi controllare e.statusCode per errori specifici come CommonStatusCodes.SIGN_IN_CANCELLED
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier, onGoogleSignInClicked: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onGoogleSignInClicked) {
            Text("Accedi con Google")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PinguTheme {
        LoginScreen(onGoogleSignInClicked = {})
    }
}