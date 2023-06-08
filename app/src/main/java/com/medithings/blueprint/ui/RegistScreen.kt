package com.medithings.blueprint.ui

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.medithings.blueprint.R
import com.medithings.blueprint.pref.LocalPrefData
import com.medithings.blueprint.support.*
import com.medithings.blueprint.ui.scanner.*
import kotlin.system.exitProcess

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun RegistScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
    scannerViewModel: ScannerViewModel = composableActivityViewModel(),
) {
    val localPrefData = LocalPrefData(context = LocalContext.current)
    val localContext = LocalContext.current
    val scope = rememberCoroutineScope()

    val activity = LocalContext.current as? Activity

    val devices = mainViewModel.devicesLiveData.observeAsState()

    val scanningState by scannerViewModel.state.collectAsStateWithLifecycle(ScanningState.Loading)

    val twoButtonDialog = remember {
        mutableStateOf(TwoButtonDialog(false))
    }

    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = {
            mainViewModel.tryBondQr(it.contents.takeLast(6))
        }
    )


    val serialInputShow = remember {
        mutableStateOf(false)
    }

    mainViewModel.actionEvent.observeEvent(LocalLifecycleOwner.current) {
        when (it) {
            is MainViewModel.ViewAction.BondFailed -> {
                twoButtonDialog.value = TwoButtonDialog(
                    true,
                    title = "본딩 실패",
                    description = "본딩에 실패했습니다. Pin을 확인하신 뒤 다시 시도해주세요",
                    onClickedConfirm = {
                        twoButtonDialog.value = TwoButtonDialog(false)
                    },
                    confirmButton = "확인",
                )
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        BackHandler {
            exitProcess(0)
        }
        if (scanningState is ScanningState.DevicesDiscovered) {
            if (twoButtonDialog.value.isShowing.not() && serialInputShow.value.not()) { // 디바이스 인풋 상황일땐 refresh 시키지 않게 방어로직 추가함
                (scanningState as ScanningState.DevicesDiscovered).let {
                    mainViewModel.refreshDevice(it)
                }
            }
        }

        FragmentTitle(text = stringResource(R.string.regist1))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = String.format(stringResource(R.string.regist2), devices.value?.size ?: 0),
            fontSize = 18.textDp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(
                start = 20.dp,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        DialogSurface(
            bgColor = black12Disable
        ) {
            Text(
                lineHeight = 24.textDp,
                text = stringResource(R.string.regist3),
                fontSize = 16.textDp,
                fontWeight = FontWeight.Normal,
                color = black65,
                modifier = Modifier.padding(
                    20.dp
                ),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = stringResource(R.string.regist4),
            onClick = {
                if ((devices.value?.size ?: 0) > 0) {
                    twoButtonDialog.value = TwoButtonDialog(
                        true,
                        title = localContext.getString(R.string.regist6),
                        description = localContext.getString(R.string.regist7),
                        onClickedConfirm = {
                            scanLauncher.launch(ScanOptions().apply {
                                setPrompt("Please scan the QR code")
                            })
                            twoButtonDialog.value = TwoButtonDialog(false)
                        },
                        confirmButton = localContext.getString(R.string.regist8),
                        onClickedCancel = {
                            twoButtonDialog.value = TwoButtonDialog(false)
                            serialInputShow.value = true
                        },
                        cancelButton = localContext.getString(R.string.regist9),
                    )
                } else {
                    twoButtonDialog.value = TwoButtonDialog(
                        true,
                        title = "연결 실패",
                        description = "연결 가능한 디바이스가 없습니다.",
                        subDescription = "블루투스 활성화를 확인해 주세요.",
                        onClickedConfirm = {
                            twoButtonDialog.value = TwoButtonDialog(false)
                            activity?.finish()
                        },
                        confirmButton = "어플리케이션 종료",
                    )
                }
//                if (devices.value?.size ?: 0 > 0) {
//                    twoButtonDialog.value = TwoButtonDialog(
//                        true,
//                        title = "시리얼 넘버 등록",
//                        description = "QR 혹은 직접 넘버 입력을 통해 시리얼 넘버를 입력해 주세요.",
//                        onClickedConfirm = {
//                            scanLauncher.launch(ScanOptions().apply {
//                                setOrientationLocked(false)
//                                setPrompt("QR코드를 스캔해주세요")
//                            })
//                            twoButtonDialog.value = TwoButtonDialog(false)
//                        },
//                        confirmButton = "QR 스캔",
//                        onClickedCancel = {
//                            twoButtonDialog.value = TwoButtonDialog(false)
//                            serialInputShow.value = true
//                        },
//                        cancelButton = "직접 입력",
//                    )
//                } else {
//
//                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = stringResource(R.string.regist5),
            onClick = {
//                scope.launch {
//                    mainViewModel.navigateToIntro()
//                }
            }
        )
        OpenDialog(twoButtonDialog = twoButtonDialog.value) {
            twoButtonDialog.value = TwoButtonDialog(false)
        }
        if (serialInputShow.value) {
            var textValue by remember { mutableStateOf(TextFieldValue("")) }

            Dialog(onDismissRequest = {}) {
                DialogSurface {
                    Column {
                        VerticalMargin(dp = 32.dp)
                        dialogTitle(text = localContext.getString(R.string.regist10))
                        TextField(
                            value = textValue,
                            onValueChange = { newText -> textValue = newText },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = black65,
                                textColor = black65,
                            ),
                            placeholder = { Text("input serial") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                        )

                        VerticalMargin(dp = 20.dp)
                        Divider()
                        VerticalMargin(dp = 12.dp)
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp)

                        ) {
                            Column {}
                            PrimaryButton(
                                onClick = {
                                    serialInputShow.value = false
                                    mainViewModel.tryBondCustom(textValue.text)
                                }, text = localContext.getString(R.string.regist11),
                                contentsModifier = Modifier.wrapContentWidth(),
                                modifier = Modifier
                                    .wrapContentHeight()
                            )
                        }
                        VerticalMargin(dp = 12.dp)
                    }
                }
            }
        }
    }
}