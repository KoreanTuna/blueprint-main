package com.medithings.blueprint.ui.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.medithings.blueprint.model.DiscoveredBluetoothDevice
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


sealed class ScanningState {

    object Loading : ScanningState()

    data class Error(val errorCode: Int) : ScanningState()

    data class DevicesDiscovered(val devices: List<DiscoveredBluetoothDevice>) : ScanningState() {
        val bonded: List<DiscoveredBluetoothDevice> = devices.filter { it.isBonded }

        val notBonded: List<DiscoveredBluetoothDevice> = devices.filter { !it.isBonded }

        fun size() = bonded.size + notBonded.size

        fun bondedSize() = bonded.size

        fun isEmpty(): Boolean = devices.isEmpty()
    }

    fun isRunning(): Boolean {
        return this is Loading || this is DevicesDiscovered
    }
}


@ViewModelScoped
class ScannerRepository @Inject internal constructor(
    private val devicesDataStore: DevicesDataStore
) {
    @SuppressLint("MissingPermission")
    fun getScannerState(): Flow<ScanningState> =
        callbackFlow {
            val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    devicesDataStore.addNewDevice(result)
                    trySend(ScanningState.DevicesDiscovered(devicesDataStore.devices))
                }

                override fun onBatchScanResults(results: MutableList<ScanResult>) {
                    results.forEach {
                        devicesDataStore.addNewDevice(it)
                    }
                    if (results.isNotEmpty()) {
                        trySend(ScanningState.DevicesDiscovered(devicesDataStore.devices))
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    trySend(ScanningState.Error(errorCode))
                }
            }


            scanner.startScan(
                listOf(ScanFilter.Builder().build()),
                ScanSettings.Builder()
                    .setReportDelay(400)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build(),
                scanCallback
            )

            awaitClose {
                scanner.stopScan(scanCallback)
            }
        }

    fun clear() {
        devicesDataStore.clear()
    }
}
