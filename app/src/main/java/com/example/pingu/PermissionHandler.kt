package com.example.pingu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

enum class PermissionStatus {
    /** Permission has been granted by the user. */
    GRANTED,

    /** Permission has been denied by the user at least once. It's not permanently denied yet. */
    DENIED,

    /**
     * Permission has been denied by the user, and they also selected "Don't ask again".
     * Or, the permission is restricted by policy.
     * The app should guide the user to app settings.
     */
    PERMANENTLY_DENIED,

    /**
     * Permission has been denied, but the system recommends showing a rationale
     * to the user explaining why the permission is needed before requesting again.
     * This is typically after the first denial.
     */
    SHOW_RATIONALE
}

/**
 * Represents the state of a runtime permission and provides a way to request it.
 *
 * @param permission The Android permission string (e.g., Manifest.permission.CAMERA).
 * @param onPermissionResult Optional callback invoked with the [PermissionStatus] after a
 *                           permission request or when the status changes due to external factors
 *                           (e.g., user changing permission in settings).
 */
@Composable
fun rememberPermissionState(
    permission: String,
    onPermissionResult: ((PermissionStatus) -> Unit)? = null
): PermissionHandlerState {
    val context = LocalContext.current
    val activity = context.findActivity()

    // Internal state to track the permission
    var internalPermissionStatus by remember {
        mutableStateOf(getInitialPermissionStatus(activity, permission))
    }

    // This derived state ensures that the external onPermissionResult is called
    // only when the status actually changes, and also provides the status to PermissionHandlerState.
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
            // If denied, check if we should show rationale or if it's permanently denied
            // The activity check is important here.
            if (activity != null && activity.shouldShowRequestPermissionRationale(permission)) {
                PermissionStatus.SHOW_RATIONALE
            } else {
                // This could be PERMANENTLY_DENIED or just DENIED if rationale isn't applicable
                // (e.g., first-time denial on some OS versions, or policy restricted).
                // For simplicity, we'll map it to PERMANENTLY_DENIED if rationale is false
                // as the action (go to settings) is often the same.
                // A more granular state could be introduced if needed.
                PermissionStatus.PERMANENTLY_DENIED
            }
        }
    }

    // Observe lifecycle to update permission status if changed externally (e.g., from settings)
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

    return remember(permission, currentStatus) { // Re-remember if permission string or status changes
        PermissionHandlerState(
            permission = permission,
            status = currentStatus,
            launchPermissionRequest = {
                // It's good practice to only launch if not already granted.
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
) {
    /** Convenience getter to check if permission is currently granted. */
    val isGranted: Boolean
        get() = status == PermissionStatus.GRANTED
}


private fun getInitialPermissionStatus(activity: Activity?, permission: String): PermissionStatus {
    if (activity == null) {
        // Cannot determine status without an activity context for checking rationale.
        // Default to DENIED, assuming it's not granted. The lifecycle observer will correct if needed.
        return PermissionStatus.DENIED
    }
    return when {
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED ->
            PermissionStatus.GRANTED
        // At this initial check, if not granted, we can't definitively know if it's SHOW_RATIONALE
        // or PERMANENTLY_DENIED without a request having been made.
        // However, we can check shouldShowRequestPermissionRationale for a hint.
        // If shouldShowRequestPermissionRationale is true, it means the user denied it previously.
        activity.shouldShowRequestPermissionRationale(permission) ->
            PermissionStatus.SHOW_RATIONALE
        else ->
            // If not granted and rationale is not needed, it's either never asked,
            // or denied with "don't ask again", or policy restricted.
            // We'll default to DENIED here. The launcher callback will refine to PERMANENTLY_DENIED if applicable.
            PermissionStatus.DENIED
    }
}

/**
 * Utility function to find an [Activity] from a [Context].
 * Needed for `shouldShowRequestPermissionRationale`.
 */
fun Context.findActivity(): Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null // Could not find an Activity.
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