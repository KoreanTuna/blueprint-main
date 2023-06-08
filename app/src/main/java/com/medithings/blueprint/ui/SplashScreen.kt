package com.medithings.blueprint.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.medithings.blueprint.R
import com.medithings.blueprint.Start
import com.medithings.blueprint.support.composableActivityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SplashScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
) {

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.medilight_logo),
            contentDescription = null,
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .wrapContentSize()
                .padding(8.dp)
        )
    }

    scope.launch {
        delay(1000)
        //mainViewModel.navigateTo(Demo.route)
        mainViewModel.navigateTo(Start.route)
    }
}