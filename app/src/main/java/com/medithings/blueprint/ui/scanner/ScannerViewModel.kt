package com.medithings.blueprint.ui.scanner

import android.os.ParcelUuid
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import javax.inject.Inject

private const val FILTER_RSSI = -50 // [dBm]

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val scannerRepository: ScannerRepository,
) : ViewModel() {

    init {
        setFilterUuid(ParcelUuid(UART_SERVICE_UUID))
    }

    private var uuid: ParcelUuid? = null

    val filterConfig = MutableStateFlow(
        DevicesScanFilter(
            filterUuidRequired = true,
            filterNearbyOnly = false,
            filterWithNames = true
        )
    )

    private val scannerState = scannerRepository.getScannerState()

    val state = filterConfig
        .combine(scannerState) { config, result ->
            Timber.i("medithings ===  state: $result")
            when (result) {
                is ScanningState.DevicesDiscovered -> {
                    //result.devices.firstOrNull()?.unBondBluetoothDevice() Sr 날려서 disconnect 시키면서 bonding 해제
                    result.applyFilters(config)
                }
                else -> result
            }
        }
    // This can't be observed in View Model Scope, as it can exist even when the
    // scanner is not visible. Scanner state stops scanning when it is not observed.
    // .stateIn(viewModelScope, SharingStarted.Lazily, ScanningState.Loading)

    private fun ScanningState.DevicesDiscovered.applyFilters(config: DevicesScanFilter) =
        ScanningState.DevicesDiscovered(
            devices
//                .filter {
//                    uuid == null ||
//                            config.filterUuidRequired == false ||
//                            it.scanResult?.scanRecord?.serviceUuids?.contains(uuid) == true
//                }
//            .filter { !config.filterNearbyOnly || it.highestRssi >= FILTER_RSSI }
//            .filter { !config.filterWithNames || it.hadName }
        )

    fun setFilterUuid(uuid: ParcelUuid?) {
        this.uuid = uuid
        if (uuid == null) {
            filterConfig.value = filterConfig.value.copy(filterUuidRequired = null)
        }
    }

    fun setFilter(config: DevicesScanFilter) {
        this.filterConfig.value = config
    }

    fun refresh() {
        scannerRepository.clear()
    }

    override fun onCleared() {
        super.onCleared()
        scannerRepository.clear()
    }
}