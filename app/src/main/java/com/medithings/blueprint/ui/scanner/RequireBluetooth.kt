package com.medithings.blueprint.ui.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.medithings.blueprint.support.Available
import com.medithings.blueprint.support.FeatureNotAvailableReason
import com.medithings.blueprint.support.NotAvailable
import com.medithings.blueprint.support.PermissionViewModel
import timber.log.Timber

@Composable
fun RequireBluetooth(
    onChanged: (Boolean) -> Unit = {},
    contentWithoutBluetooth: @Composable (FeatureNotAvailableReason) -> Unit = {
        NoBluetoothView(reason = it)
    },
    content: @Composable () -> Unit,
) {
    val viewModel = hiltViewModel<PermissionViewModel>()
    val state by viewModel.bluetoothState.collectAsState()

    Timber.d("state=$state")
    onChanged(state is Available)

    when (val s = state) {
        Available -> content()
        is NotAvailable -> contentWithoutBluetooth(s.reason)
    }
}

@Composable
private fun NoBluetoothView(
    reason: FeatureNotAvailableReason,
) {
    Timber.d("NoBluetoothView(reason=$reason)")
    when (reason) {
        FeatureNotAvailableReason.NOT_AVAILABLE -> BluetoothNotAvailableView()
        FeatureNotAvailableReason.PERMISSION_REQUIRED ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                BluetoothPermissionRequiredView()
            }
        FeatureNotAvailableReason.DISABLED -> BluetoothDisabledView()
    }
}

@Composable
internal fun BluetoothDisabledView() {
    WarningView(
        imageVector = Icons.Default.BluetoothDisabled,
        title = "블루투스 사용불가",
        hint = "블루투스가 꺼져있습니다.\n블루투스를 켜주세요.",
        modifier = Modifier
            .fillMaxSize()
        //.verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current
        Button(onClick = { enableBluetooth(context) }) {
            Text(text = "활성화")
        }
    }
}

@SuppressLint("MissingPermission")
private fun enableBluetooth(context: Context) {
    context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
}

@Composable
internal fun BluetoothNotAvailableView() {
    WarningView(
        imageVector = Icons.Default.BluetoothDisabled,
        title = "블루투스가 되지 않습니다.",
        hint = "Bluetooth is not available on your phone.\nWe cannot provide you the feature.",
        modifier = Modifier
            .fillMaxSize()
        //.verticalScroll(rememberScrollState())
    )
}

@Composable
fun WarningView(
    imageVector: ImageVector,
    title: String,
    hint: String,
    modifier: Modifier = Modifier,
    hintTextAlign: TextAlign? = TextAlign.Center,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = modifier.size(24.dp),
            )
        }

        Text(text = title)

        Text(text = hint, textAlign = hintTextAlign)

        content()
    }
}