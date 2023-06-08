package com.medithings.blueprint.ui.scanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.medithings.blueprint.support.Available
import com.medithings.blueprint.support.FeatureNotAvailableReason
import com.medithings.blueprint.support.NotAvailable
import com.medithings.blueprint.support.PermissionViewModel

@Composable
fun RequireLocation(
    onChanged: (Boolean) -> Unit = {},
    contentWithoutLocation: @Composable () -> Unit = { LocationPermissionRequiredView() },
    content: @Composable (isLocationRequiredAndDisabled: Boolean) -> Unit,
) {
    val viewModel = hiltViewModel<PermissionViewModel>()
    val state by viewModel.locationPermission.collectAsState()

    onChanged(state is Available || (state as NotAvailable).reason == FeatureNotAvailableReason.DISABLED)

    when (val s = state) {
        is NotAvailable -> when (s.reason) {
            FeatureNotAvailableReason.DISABLED -> content(true)
            else -> contentWithoutLocation()
        }
        Available -> content(false)
    }
}