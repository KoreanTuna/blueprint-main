package com.medithings.blueprint.ui.scanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.medithings.blueprint.support.PermissionViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
internal fun BluetoothPermissionRequiredView() {
    val viewModel = hiltViewModel<PermissionViewModel>()
    val activity = LocalContext.current as Activity
    var permissionDenied by remember {
        mutableStateOf(
            viewModel.isBluetoothScanPermissionDeniedForever(
                activity
            )
        )
    }

    WarningView(
        imageVector = Icons.Default.BluetoothDisabled,
        title = "블루투스 권한",
        hint = "블루투스 권한이 필요합니다.",
        modifier = Modifier
            .fillMaxWidth()
        //.verticalScroll(rememberScrollState())
    ) {
        val requiredPermissions = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
        )

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.markBluetoothPermissionRequested()
            permissionDenied = viewModel.isBluetoothScanPermissionDeniedForever(activity)
            viewModel.refreshBluetoothPermission()
        }

        if (!permissionDenied) {
            Button(onClick = { launcher.launch(requiredPermissions) }) {
                Text(text = "권한 얻기")
            }
        } else {
            Button(onClick = { openPermissionSettings(activity) }) {
                Text(text = "설정에서 직접 장치 검색을 해주세요.")
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