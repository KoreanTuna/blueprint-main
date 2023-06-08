package com.medithings.blueprint.ui.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterView(
    config: DevicesScanFilter,
    onChanged: (DevicesScanFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier,
    ) {
        config.filterUuidRequired?.let {
            ElevatedFilterChip(
                selected = !it,
                onClick = { onChanged(config.copy(filterUuidRequired = !it)) },
                label = { Text(text = "전체") },
                modifier = Modifier.padding(end = 8.dp),
                leadingIcon = {
                    if (!it) {
                        Icon(Icons.Default.Done, contentDescription = "")
                    } else {
                        Icon(Icons.Default.Widgets, contentDescription = "")
                    }
                },
            )
        }
        config.filterNearbyOnly.let {
            ElevatedFilterChip(
                selected = it,
                onClick = { onChanged(config.copy(filterNearbyOnly = !it)) },
                label = { Text(text = "주변") },
                modifier = Modifier.padding(end = 8.dp),
                leadingIcon = {
                    if (it) {
                        Icon(Icons.Default.Done, contentDescription = "")
                    } else {
                        Icon(Icons.Default.Wifi, contentDescription = "")
                    }
                },
            )
        }
        config.filterWithNames.let {
            ElevatedFilterChip(
                selected = it,
                onClick = { onChanged(config.copy(filterWithNames = !it)) },
                label = { Text(text = "이름") },
                modifier = Modifier.padding(end = 8.dp),
                leadingIcon = {
                    if (it) {
                        Icon(Icons.Default.Done, contentDescription = "")
                    } else {
                        Icon(Icons.Default.Label, contentDescription = "")
                    }
                },
            )
        }
    }
}