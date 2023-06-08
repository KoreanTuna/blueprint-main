package com.medithings.blueprint.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DiscoveredBluetoothDevice(
    val device: BluetoothDevice,
    val scanResult: ScanResult? = null,
    val name: String? = null,
    val hadName: Boolean = name != null,
    val lastScanResult: ScanResult? = null,
    val rssi: Int = 0,
    val previousRssi: Int = 0,
    val highestRssi: Int = Integer.max(rssi, previousRssi),
) : Parcelable {

    fun hasRssiLevelChanged(): Boolean {
        val newLevel =
            if (rssi <= 10) 0 else if (rssi <= 28) 1 else if (rssi <= 45) 2 else if (rssi <= 65) 3 else 4
        val oldLevel =
            if (previousRssi <= 10) 0 else if (previousRssi <= 28) 1 else if (previousRssi <= 45) 2 else if (previousRssi <= 65) 3 else 4
        return newLevel != oldLevel
    }

    fun update(scanResult: ScanResult): DiscoveredBluetoothDevice = copy(
        device = scanResult.device,
        lastScanResult = scanResult,
        name = scanResult.scanRecord?.deviceName,
        hadName = hadName || name != null,
        previousRssi = rssi,
        rssi = scanResult.rssi,
        highestRssi = if (highestRssi > rssi) highestRssi else rssi
    )

    fun matches(scanResult: ScanResult) = device.address == scanResult.device.address

    @SuppressLint("MissingPermission")
    fun createBond(pinString: String = ""): Boolean {
        return device.createBond()
    }

    fun unBondBluetoothDevice() {
        val pair = device.javaClass.getMethod("removeBond")
        pair.invoke(device)
    }

    fun isConnected() = try {
        val pair = device.javaClass.getMethod("isConnected")
        pair.invoke(device) as Boolean
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    val displayName: String?
        @SuppressLint("MissingPermission")
        get() = when {
            name?.isNotEmpty() == true -> name
            device.name?.isNotEmpty() == true -> device.name
            else -> null
        }

    val address: String
        get() = device.address

    val displayNameOrAddress: String
        get() = displayName ?: address

    val bondingState: Int
        @SuppressLint("MissingPermission")
        get() = device.bondState

    val isBonded: Boolean
        get() = bondingState == BluetoothDevice.BOND_BONDED

    override fun hashCode() = device.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other is DiscoveredBluetoothDevice) {
            return device == other.device
        }
        return super.equals(other)
    }
}

fun ScanResult.toDiscoveredBluetoothDevice() = DiscoveredBluetoothDevice(
    device = device,
    scanResult = this,
    name = scanRecord?.deviceName,
    previousRssi = rssi,
    rssi = rssi
)