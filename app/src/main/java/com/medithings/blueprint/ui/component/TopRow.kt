package com.medithings.blueprint.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.medithings.blueprint.BluePrintDestination
import com.medithings.blueprint.TopType

@Composable
fun TopRow(
    currentScreen: BluePrintDestination,
    navController: NavHostController
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize()
            .height(TabHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (currentScreen.topType) {
            is TopType.BACK -> IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = currentScreen.route)
            }
            is TopType.Icon -> Icon(imageVector = (currentScreen.topType as TopType.Icon).icon, contentDescription = currentScreen.route)
            else -> Unit
        }
        Spacer(modifier = Modifier.width(4.dp))
        if (currentScreen.screenTitle.isNotEmpty()) {
            Text(
                text = currentScreen.screenTitle, style = MaterialTheme.typography.h6)
        }
    }
}

private val TabHeight = 56.dp