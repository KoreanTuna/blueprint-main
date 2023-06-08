package com.medithings.blueprint.support

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class PermissionViewModel @Inject internal constructor(
    internetManager: InternetStateManager,
    private val bluetoothManager: BluetoothStateManager,
    private val locationManager: LocationStateManager,
) : ViewModel() {
    val bluetoothState = bluetoothManager.bluetoothState()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            NotAvailable(FeatureNotAvailableReason.NOT_AVAILABLE)
        )

    val locationPermission = locationManager.locationState()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            NotAvailable(FeatureNotAvailableReason.NOT_AVAILABLE)
        )

    val internetPermission = internetManager.networkState()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            NotAvailable(FeatureNotAvailableReason.NOT_AVAILABLE)
        )

    fun refreshBluetoothPermission() {
        bluetoothManager.refreshPermission()
    }

    fun refreshLocationPermission() {
        locationManager.refreshPermission()
    }

    fun markLocationPermissionRequested() {
        locationManager.markLocationPermissionRequested()
    }

    fun markBluetoothPermissionRequested() {
        bluetoothManager.markBluetoothPermissionRequested()
    }

    fun isBluetoothScanPermissionDeniedForever(activity: Activity): Boolean {
        return bluetoothManager.isBluetoothScanPermissionDeniedForever(activity)
    }

    fun isLocationPermissionDeniedForever(activity: Activity): Boolean {
        return locationManager.isLocationPermissionDeniedForever(activity)
    }
}