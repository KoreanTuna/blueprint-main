package com.medithings.blueprint.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medithings.blueprint.R
import com.medithings.blueprint.pref.LocalPrefData
import com.medithings.blueprint.support.*
import com.medithings.blueprint.ui.scanner.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun StartScreen(
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
        ) {
            StartContent()
        }
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun StartContent(
    mainViewModel: MainViewModel = composableActivityViewModel(),
    scannerViewModel: ScannerViewModel = composableActivityViewModel(),
) {
//    val viewModel = hiltViewModel<ScannerViewModel>()
//        .apply { setFilterUuid(ParcelUuid(UART_SERVICE_UUID)) }

    val localPrefData = LocalPrefData(context = LocalContext.current)

    val state by scannerViewModel.state.collectAsStateWithLifecycle(ScanningState.Loading)
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        // TODO 추후에 풀어야함
        val isOpenDialog = remember { mutableStateOf(false) }
        val isDebugDialog = remember { mutableStateOf(false) }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable {
                    isOpenDialog.value = true
                }
                .align(alignment = Alignment.TopCenter)
                .padding(8.dp),
            text = "Debug",
            color = black4Box
        )

        if (isOpenDialog.value.not()) {
            when (state) {
                is ScanningState.Loading -> Image(
                    painter = painterResource(id = R.drawable.medilight_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                        .wrapContentSize()
                        .padding(8.dp)
                )
                is ScanningState.DevicesDiscovered -> (state as ScanningState.DevicesDiscovered).let {
                    Image(
                        painter = painterResource(id = R.drawable.medilight_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .align(alignment = Alignment.Center)
                            .wrapContentSize()
                            .padding(8.dp)
                    )
                    if (it.bondedSize() > 0) {
                        // bonded device 있음
                        if (localPrefData.isIntroShown()) {
                            scope.launch {
                                mainViewModel.connectDevice(it)
                            }
                        } else {
                            scope.launch {
                                mainViewModel.initDeviceOnly(it)
                            }
                        }
                    } else {
                        scope.launch {
                            mainViewModel.initDeviceOnly(it)
                        }
                    }
                }
                is ScanningState.Error -> ScanErrorView((state as ScanningState.Error).errorCode)
            }
        } else {
            if (isDebugDialog.value.not()) {
                var textValue by remember { mutableStateOf(TextFieldValue("")) }

                Dialog(onDismissRequest = {
                    isOpenDialog.value = false
                }) {
                    DialogSurface {
                        BackHandler {
                            isOpenDialog.value = false
                        }
                        Column {
                            VerticalMargin(dp = 32.dp)
                            dialogTitle(text = "Debug 모드 비밀번호 입력")
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
                                placeholder = { "관리자 비밀번호 입력" },
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
                                        if (textValue.text == "1234") {
                                            isDebugDialog.value = true
                                        } else {
                                            mainViewModel.showToast("비밀번호가 잘못되었습니다")
                                            isOpenDialog.value = false
                                        }
                                    }, text = "확인",
                                    contentsModifier = Modifier.wrapContentWidth(),
                                    modifier = Modifier
                                        .wrapContentHeight()
                                )
                            }
                            VerticalMargin(dp = 12.dp)
                        }
                    }
                }
            } else {

                Dialog(onDismissRequest = {
                    isOpenDialog.value = false
                    isDebugDialog.value = false
                }) {
                    DebugValueDialog(
                        mainViewModel = mainViewModel,
                        localPrefData = localPrefData, onBack = {
                            isOpenDialog.value = false
                            isDebugDialog.value = false
                        })
                }
            }
        }
    }
}


@Composable
fun DebugValueDialog(
    mainViewModel: MainViewModel = composableActivityViewModel(),
    localPrefData: LocalPrefData,
    onBack: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var valiExpanded by remember { mutableStateOf(false) }

    var trainValue by remember {
        mutableStateOf(
            TextFieldValue(
                localPrefData.getTrainCount().toString()
            )
        )
    }
    var validationValue by remember {
        mutableStateOf(
            TextFieldValue(
                localPrefData.getValidationCount().toString()
            )
        )
    }
    var mainInferValue by remember {
        mutableStateOf(
            TextFieldValue(
                localPrefData.getMainInferCount().toString()
            )
        )
    }
    var epochValue by remember {
        mutableStateOf(
            TextFieldValue(
                localPrefData.getEpochCount().toString()
            )
        )
    }
    var batchValue by remember {
        mutableStateOf(
            TextFieldValue(
                localPrefData.getBatchSize().toString()
            )
        )
    }
    var patientValue by remember {
        mutableStateOf(
            TextFieldValue(
                localPrefData.getPatientCount().toString()
            )
        )
    }

    DialogSurface {
        BackHandler {
            onBack.invoke()
        }

        Column(
            modifier = Modifier
                .wrapContentSize()
        ) {
            VerticalMargin(dp = 32.dp)
            dialogTitle(text = "Debug-관리자모드, 숫자만 입력")
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "trainValue",
                color = black65,
                fontSize = 16.textDp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            TextField(
                value = trainValue,
                onValueChange = { newText -> trainValue = newText },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = black65,
                    textColor = black65,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
            )
            Text(
                text = "메인학습 sj 횟수(default:2)",
                color = black65,
                fontSize = 16.textDp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            TextField(
                value = mainInferValue,
                onValueChange = { newText -> mainInferValue = newText },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = black65,
                    textColor = black65,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
            )
            Text(
                text = "epochValue",
                color = black65,
                fontSize = 16.textDp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            TextField(
                value = epochValue,
                onValueChange = { newText -> epochValue = newText },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = black65,
                    textColor = black65,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
            )

            Text(
                text = "patientMaxValue",
                color = black65,
                fontSize = 16.textDp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            TextField(
                value = patientValue,
                onValueChange = { newText -> patientValue = newText },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = black65,
                    textColor = black65,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
            )
            Box {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        text = "batchSize = ${batchValue.text}",
                        color = black65,
                        fontSize = 16.textDp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            batchValue = TextFieldValue("1")
                            expanded = false
                        }
                    ) {
                        Text(
                            text = "1",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            batchValue = TextFieldValue("2")
                            expanded = false
                        }
                    ) {
                        Text(
                            text = "2",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            batchValue = TextFieldValue("16")
                            expanded = false
                        }
                    ) {
                        Text(
                            text = "16",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            batchValue = TextFieldValue("32")
                            expanded = false
                        }
                    ) {
                        Text(
                            text = "32",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            batchValue = TextFieldValue("64")
                            expanded = false
                        }
                    ) {
                        Text(
                            text = "64",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            batchValue = TextFieldValue("128")
                            expanded = false
                        }
                    ) {
                        Text(
                            text = "128",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            Box {
                TextButton(onClick = { valiExpanded = !valiExpanded }) {
                    Text(
                        text = "validationValue = ${validationValue.text}",
                        color = black65,
                        fontSize = 16.textDp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                DropdownMenu(
                    expanded = valiExpanded,
                    onDismissRequest = { valiExpanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            validationValue = TextFieldValue("1")
                            valiExpanded = false
                        }
                    ) {
                        Text(
                            text = "1",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            validationValue = TextFieldValue("2")
                            valiExpanded = false
                        }
                    ) {
                        Text(
                            text = "2",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            validationValue = TextFieldValue("16")
                            valiExpanded = false
                        }
                    ) {
                        Text(
                            text = "16",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            validationValue = TextFieldValue("32")
                            valiExpanded = false
                        }
                    ) {
                        Text(
                            text = "32",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            validationValue = TextFieldValue("64")
                            valiExpanded = false
                        }
                    ) {
                        Text(
                            text = "64",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            validationValue = TextFieldValue("128")
                            valiExpanded = false
                        }
                    ) {
                        Text(
                            text = "128",
                            color = black65,
                            fontSize = 16.textDp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

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
                        localPrefData.setTrainCount(trainValue.text.toInt())
                        localPrefData.setValidationCount(validationValue.text.toInt())
                        localPrefData.setEpochCount(epochValue.text.toInt())
                        localPrefData.setBatchSize(batchValue.text.toInt())
                        localPrefData.setPatientCount(patientValue.text.toInt())
                        localPrefData.setMainInferCount(mainInferValue.text.toInt())
                        onBack.invoke()
                        mainViewModel.showToast("설정이 적용되었습니다.")
                    }, text = "확인",
                    contentsModifier = Modifier.wrapContentWidth(),
                    modifier = Modifier
                        .wrapContentHeight()
                )
            }
            VerticalMargin(dp = 12.dp)
        }
    }
}