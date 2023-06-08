package com.medithings.blueprint.ui.scanner

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.medithings.blueprint.model.DiscoveredBluetoothDevice
import com.medithings.blueprint.model.toDiscoveredBluetoothDevice
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DevicesDataStore @Inject constructor() {
    val devices = mutableListOf<DiscoveredBluetoothDevice>()
    val data = MutableStateFlow(devices.toList())

    @SuppressLint("MissingPermission")
    fun addNewDevice(scanResult: ScanResult) {
        if (scanResult.device.name == null || scanResult.device.name.contains("Bladder Patch")
                .not()
        ) return // Bladder Path만 검색하게함
        devices.firstOrNull { it.device == scanResult.device }?.let { device ->
            val i = devices.indexOf(device)
            devices.set(i, device.update(scanResult))
        } ?: scanResult.toDiscoveredBluetoothDevice().also { devices.add(it) }

        data.value = devices.toList()
    }

    fun clear() {
        devices.clear()
        data.value = devices
    }
}

data class DevicesScanFilter(
    val filterUuidRequired: Boolean?,
    val filterNearbyOnly: Boolean,
    val filterWithNames: Boolean
)