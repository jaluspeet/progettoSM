package com.example.common.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

enum class PermissionStatus {
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED,
    SHOW_RATIONALE
}

/**
 * Represents the state of a runtime permission and provides a way to request it.
 *
 * @param permission The Android permission string (e.g., Manifest.permission.CAMERA).
 * @param onPermissionResult Optional callback invoked with the [PermissionStatus] after a
 *                           permission request or when the status changes
 */
@Composable
fun rememberPermissionState(
    permission: String,
    onPermissionResult: ((PermissionStatus) -> Unit)? = null
): PermissionHandlerState {
    val context = LocalContext.current
    val activity = context.findActivity()

    var internalPermissionStatus by remember {
        mutableStateOf(getInitialPermissionStatus(activity, permission))
    }

    val currentStatus by remember(internalPermissionStatus) {
        derivedStateOf {
            onPermissionResult?.invoke(internalPermissionStatus)
            internalPermissionStatus
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        internalPermissionStatus = if (isGranted) {
            PermissionStatus.GRANTED
        } else {
            if (activity != null && activity.shouldShowRequestPermissionRationale(permission)) {
                PermissionStatus.SHOW_RATIONALE
            } else {
                PermissionStatus.PERMANENTLY_DENIED
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, permission) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val newStatus = getInitialPermissionStatus(activity, permission)
                if (newStatus != internalPermissionStatus) {
                    internalPermissionStatus = newStatus
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return remember(permission, currentStatus) {
        PermissionHandlerState(
            permission = permission,
            status = currentStatus,
            launchPermissionRequest = {
                if (currentStatus != PermissionStatus.GRANTED) {
                    permissionLauncher.launch(permission)
                }
            }
        )
    }
}

/**
 * A state object that can be hoisted to control and observe permission status.
 *
 * @property permission The Android permission this state is for.
 * @property status The current [PermissionStatus].
 * @property launchPermissionRequest A function to trigger the runtime permission request.
 */
@Stable
data class PermissionHandlerState(
    val permission: String,
    val status: PermissionStatus,
    val launchPermissionRequest: () -> Unit
)


private fun getInitialPermissionStatus(activity: Activity?, permission: String): PermissionStatus {
    if (activity == null) {
        return PermissionStatus.DENIED
    }
    return when {
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED ->
            PermissionStatus.GRANTED

        activity.shouldShowRequestPermissionRationale(permission) ->
            PermissionStatus.SHOW_RATIONALE
        else ->
            PermissionStatus.DENIED
    }
}

/**
 * Utility function to find an [Activity] from a [Context].
 * Needed for `shouldShowRequestPermissionRationale`.
 */
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

/**
 * Opens the application's settings screen for the user to manually change permissions.
 *
 * @param context The current context.
 */
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}