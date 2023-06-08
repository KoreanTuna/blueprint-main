package com.medithings.blueprint.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.medithings.blueprint.R
import com.medithings.blueprint.pref.LocalPrefData
import com.medithings.blueprint.support.*
import com.medithings.blueprint.ui.scanner.*
import java.util.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun BladderLevSettingScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
) {

    val localPrefData = LocalPrefData(context = LocalContext.current)

    val _fireLev = remember {
        mutableStateOf(localPrefData.getFireLev())
    }
    val fireLev by _fireLev

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 10.dp, end = 10.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        FragmentTitle(text = stringResource(id = R.string.setting2))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            PrimaryButton(
                color = if (fireLev == 3) red100 else demoPrimary,
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                onClick = {
                    _fireLev.value = 3
                },
                text = "Lv.3",
                fontSize = 36.textDp
            )
            Spacer(modifier = Modifier.width(8.dp))
            PrimaryButton(
                color = if (fireLev == 4) red100 else demoPrimary,
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                onClick = {
                    _fireLev.value = 4
                },
                text = "Lv.4",
                fontSize = 36.textDp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            PrimaryButton(
                color = if (fireLev == 5) red100 else demoPrimary,
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                onClick = {
                    _fireLev.value = 5
                },
                text = "Lv.5",
                fontSize = 36.textDp
            )
            Spacer(modifier = Modifier.width(8.dp))
            PrimaryButton(
                color = if (fireLev == 6) red100 else demoPrimary,
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                onClick = {
                    _fireLev.value = 6
                },
                text = "Lv.6",
                fontSize = 36.textDp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            PrimaryButton(
                color = if (fireLev == 7) red100 else demoPrimary,
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                onClick = {
                    _fireLev.value = 7
                },
                text = "Lv.7",
                fontSize = 36.textDp
            )
            Spacer(modifier = Modifier.width(8.dp))
            PrimaryButton(
                color = if (fireLev == 8) red100 else demoPrimary,
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                onClick = {
                    _fireLev.value = 8
                },
                text = "Lv.8",
                fontSize = 36.textDp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            PrimaryButton(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    localPrefData.setFireLev(_fireLev.value)
                    mainViewModel.showToast("저장되었습니다.")
                    mainViewModel.navigateUp()
                },
                text = "확인",
                fontSize = 36.textDp
            )
        }
    }
}