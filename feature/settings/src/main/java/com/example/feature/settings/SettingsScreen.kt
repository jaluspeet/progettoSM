package com.example.feature.settings

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.common.login.GoogleSignInHandler

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToAppSettings: () -> Unit
) {
    val context = LocalContext.current
    val signInHelper = remember { GoogleSignInHandler(context) }

    var currentAccount by remember { mutableStateOf(signInHelper.currentAccount()) }

    val loginLauncher = rememberLauncherForActivityResult(
        contract = GoogleSignInHandler.SignInContract(),
        onResult = { account ->
            Log.d("Login", "Account: ${account?.email}, name: ${account?.displayName}")
            currentAccount = account
        }
    )

    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Welcome, ${currentAccount?.displayName ?: "username"}!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        SettingsListItem(
            title = if (currentAccount == null) "Login" else "Logout",
            icon = Icons.Filled.Person,
            onClick = {
                if (currentAccount == null) {
                    loginLauncher.launch(Unit)
                } else {
                    signInHelper.signOut().addOnCompleteListener {
                        currentAccount = null
                    }
                }
            }
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        SettingsListItem(
            title = "Camera Permissions",
            icon = Icons.Filled.ThumbUp,
            onClick = { onNavigateToAppSettings() }
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        SettingsListItem(
            title = "About",
            icon = Icons.Filled.Info,
            onClick = { showAboutDialog = true }
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About PinguApp") },
            text = { Text("Brought to you by PinguSoftware") },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun SettingsListItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
