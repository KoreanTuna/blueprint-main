package com.medithings.blueprint.ui.scanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.medithings.blueprint.model.DiscoveredBluetoothDevice

@Suppress("FunctionName")
fun LazyListScope.DeviceListItems(
    devices: ScanningState.DevicesDiscovered,
    onClick: (DiscoveredBluetoothDevice) -> Unit,
    deviceView: @Composable (DiscoveredBluetoothDevice) -> Unit,
) {
    val bondedDevices = devices.bonded
    val discoveredDevices = devices.notBonded

    if (bondedDevices.isNotEmpty()) {
        item {
            Text(
                text = "본딩된 장치",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }
        items(bondedDevices) { device ->
            ClickableDeviceItem(device, onClick, deviceView)
        }
    }

    if (discoveredDevices.isNotEmpty()) {
        item {
            Text(
                text = "발견된 장치",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }

        items(discoveredDevices) { device ->
            ClickableDeviceItem(device, onClick, deviceView)
        }
    }
}

@Composable
private fun ClickableDeviceItem(
    device: DiscoveredBluetoothDevice,
    onClick: (DiscoveredBluetoothDevice) -> Unit,
    deviceView: @Composable (DiscoveredBluetoothDevice) -> Unit,
) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .clickable { onClick(device) }
        .padding(8.dp)
    ) {
        deviceView(device)
    }
}