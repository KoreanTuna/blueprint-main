package com.medithings.blueprint.support

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

@Suppress("MemberVisibilityCanBePrivate")
internal class PermissionUtils(
    private val context: Context,
    private val dataProvider: LocalDataProvider,
) {
    val isBleEnabled: Boolean
        get() {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            return adapter != null && adapter.isEnabled
        }

    val isLocationEnabled: Boolean
        get() = if (dataProvider.isMarshmallowOrAbove) {
            val lm = context.getSystemService(LocationManager::class.java)
            LocationManagerCompat.isLocationEnabled(lm)
        } else true

    val isBluetoothAvailable: Boolean
        get() = context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)

    val isLocationPermissionRequired: Boolean
        get() = dataProvider.isMarshmallowOrAbove && !dataProvider.isSOrAbove

    val isBluetoothScanPermissionGranted: Boolean
        get() = !dataProvider.isSOrAbove ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED

    val isBluetoothConnectPermissionGranted: Boolean
        get() = !dataProvider.isSOrAbove ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED

    val isLocationPermissionGranted: Boolean
        get() = !isLocationPermissionRequired ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    val areNecessaryBluetoothPermissionsGranted: Boolean
        get() = isBluetoothScanPermissionGranted && isBluetoothConnectPermissionGranted

    fun markBluetoothPermissionRequested() {
        dataProvider.bluetoothPermissionRequested = true
    }

    fun markLocationPermissionRequested() {
        dataProvider.locationPermissionRequested = true
    }

    fun isBluetoothScanPermissionDeniedForever(activity: Activity): Boolean {
        return dataProvider.isSOrAbove &&
                !isBluetoothScanPermissionGranted && // Bluetooth Scan permission must be denied
                dataProvider.bluetoothPermissionRequested && // Permission must have been requested before
                !activity.shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)
    }

    fun isLocationPermissionDeniedForever(activity: Activity): Boolean {
        return dataProvider.isMarshmallowOrAbove &&
                !isLocationPermissionGranted // Location permission must be denied
                && dataProvider.locationPermissionRequested // Permission must have been requested before
                && !activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
