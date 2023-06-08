package com.medithings.blueprint.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
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
fun AlarmTermSettingScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
) {

    val localPrefData = LocalPrefData(context = LocalContext.current)

    val _alarmTerm = remember {
        mutableStateOf(localPrefData.getAlarmTerm())
    }
    val alarmTerm by _alarmTerm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 10.dp, end = 10.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        FragmentTitle(text = stringResource(id = R.string.setting3))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.setting3_desc1),
            fontSize = 16.textDp,
            color = black65,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            PrimaryButton(
                color = if (alarmTerm == 30) red100 else demoPrimary,
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                onClick = {
                    _alarmTerm.value = 30
                },
                text = "30분",
                fontSize = 36.textDp
            )
            Spacer(modifier = Modifier.width(8.dp))
            PrimaryButton(
                color = if (alarmTerm == 40) red100 else demoPrimary,
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                onClick = {
                    _alarmTerm.value = 40
                },
                text = "40분",
                fontSize = 36.textDp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            PrimaryButton(
                color = if (alarmTerm == 50) red100 else demoPrimary,
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                onClick = {
                    _alarmTerm.value = 50
                },
                text = "50분",
                fontSize = 36.textDp
            )
            Spacer(modifier = Modifier.width(8.dp))
            PrimaryButton(
                color = if (alarmTerm == 60) red100 else demoPrimary,
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                onClick = {
                    _alarmTerm.value = 60
                },
                text = "60분",
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
                    localPrefData.setAlarmTerm(_alarmTerm.value)
                    mainViewModel.showToast("저장되었습니다.")
                    mainViewModel.navigateUp()
                },
                text = "확인",
                fontSize = 36.textDp
            )
        }
    }
}