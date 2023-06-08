package com.medithings.blueprint.ui.scanner

import android.os.ParcelUuid
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medithings.blueprint.model.DiscoveredBluetoothDevice
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

val UART_SERVICE_UUID: UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun ScannerScreen(
    deviceItem: @Composable (DiscoveredBluetoothDevice) -> Unit = {
        DeviceListItem(it.displayName, it.address)
    }
) {
    var isScanning by rememberSaveable { mutableStateOf(false) }

    RequireBluetooth(
        onChanged = {
            isScanning = it
        }
    ) {
        RequireLocation(
            onChanged = {
                isScanning = it
            }
        ) { isLocationRequiredAndDisabled ->
            val viewModel = hiltViewModel<ScannerViewModel>()
                .apply { setFilterUuid(ParcelUuid(UART_SERVICE_UUID)) }

            val state by viewModel.state.collectAsStateWithLifecycle(ScanningState.Loading)
            val config by viewModel.filterConfig.collectAsStateWithLifecycle()
            var refreshing by remember { mutableStateOf(false) }

            val scope = rememberCoroutineScope()
            fun refresh() = scope.launch {
                refreshing = true
                viewModel.refresh()
                delay(400) // TODO remove this delay and refreshing variable after updating material dependency
                refreshing = false
            }

            Column(modifier = Modifier.fillMaxSize()) {
//                FilterView(
//                    config = config,
//                    onChanged = { viewModel.setFilter(it) },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color(0xFFEFEFEF))
//                        .padding(horizontal = 16.dp),
//                )

                val pullRefreshState = rememberPullRefreshState(
                    refreshing = refreshing,
                    onRefresh = { refresh() },
                )

                Box(
                    modifier = Modifier
                        .pullRefresh(pullRefreshState)
                        .clipToBounds()
                ) {
                    DevicesListView(
                        isLocationRequiredAndDisabled = isLocationRequiredAndDisabled,
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                        onClick = {
                            // TODO 구현해야함, 장치 선택
                        },
                        deviceItem = deviceItem,
                    )

                    PullRefreshIndicator(
                        refreshing = refreshing,
                        state = pullRefreshState,
                        Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}