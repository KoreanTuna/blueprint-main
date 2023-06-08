package com.medithings.blueprint.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.medithings.blueprint.pref.LocalPrefData
import com.medithings.blueprint.support.*
import com.medithings.blueprint.ui.scanner.*
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun IntroScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
) {
    val localPrefData = LocalPrefData(context = LocalContext.current)
    val scope = rememberCoroutineScope()

    val twoButtonDialog = remember {
        mutableStateOf(TwoButtonDialog(false))
    }

    Timber.d("IntroScreen: ${localPrefData.isIntroShown()}")

    twoButtonDialog.value = TwoButtonDialog(
        true,
        title = "부착안내페이지",
        description = "주의사항\r\n" +
                "부착방법 있어야함\r\n" +
                "사용안내 있어야 함\r\n" +
                "기기등록안내 있어야 함\r\n" +
                "QR준비 있어야함",
        onClickedConfirm = {
            //twoButtonDialog.value = TwoButtonDialog(false)
            scope.launch {
                mainViewModel.connectDevice()
            }
        },
        onClickedCancel = {
            //twoButtonDialog.value = TwoButtonDialog(false)
            localPrefData.setIntroShown()
            mainViewModel.connectDevice()
        },
        confirmButton = "다음으로",
        cancelButton = "다시보지 않기",
    )

    OpenDialog(twoButtonDialog = twoButtonDialog.value) {
        twoButtonDialog.value = TwoButtonDialog(false)
    }
}

@Composable
fun OpenDialog(twoButtonDialog: TwoButtonDialog, onBack: (() -> Unit)? = null) {
    if (twoButtonDialog.isShowing) {
        Dialog(onDismissRequest = {}) {
            DialogSurface {
                BackHandler {
                    onBack?.invoke()
                }
                TwoButtonAlertDialog(
                    title = twoButtonDialog.title,
                    description = twoButtonDialog.description,
                    confirmButton = twoButtonDialog.confirmButton,
                    cancelButton = twoButtonDialog.cancelButton,
                    onClickedConfirm = twoButtonDialog.onClickedConfirm,
                    onClickedCancel = twoButtonDialog.onClickedCancel
                )
            }
        }
    }
}

private val DefaultPadding = 4.dp