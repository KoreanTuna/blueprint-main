package com.medithings.blueprint.ui.scanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.medithings.blueprint.support.PermissionViewModel

@Composable
internal fun LocationPermissionRequiredView() {
    val viewModel = hiltViewModel<PermissionViewModel>()
    val activity = LocalContext.current as Activity
    var permissionDenied by remember {
        mutableStateOf(
            viewModel.isLocationPermissionDeniedForever(
                activity
            )
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.markLocationPermissionRequested()
        permissionDenied = viewModel.isLocationPermissionDeniedForever(activity)
        viewModel.refreshLocationPermission()
    }

    LocationPermissionRequiredView(
        permissionDenied = permissionDenied,
        onGrantClicked = {
            val requiredPermissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            launcher.launch(requiredPermissions)
        },
        onOpenSettingsClicked = { openPermissionSettings(activity) },
    )
}

@Composable
internal fun LocationPermissionRequiredView(
    permissionDenied: Boolean,
    onGrantClicked: () -> Unit,
    onOpenSettingsClicked: () -> Unit,
) {
    WarningView(
        imageVector = Icons.Default.LocationOff,
        title = "위치권한이 필요합니다.",
        hint = "위치 권한이 필요합니다",
        modifier = Modifier
            .fillMaxSize()
        //.verticalScroll(rememberScrollState())
    ) {
        if (!permissionDenied) {
            Button(onClick = onGrantClicked) {
                Text(text = "권한 얻기")
            }
        } else {
            Button(onClick = onOpenSettingsClicked) {
                Text(text = "설정")
            }
        }
    }
}

private fun openPermissionSettings(context: Context) {
    ContextCompat.startActivity(
        context,
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ),
        null
    )
}