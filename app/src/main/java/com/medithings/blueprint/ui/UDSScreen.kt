package com.medithings.blueprint.ui

import TransferLearningHelper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.medithings.blueprint.*
import com.medithings.blueprint.R
import com.medithings.blueprint.pref.LocalPrefData
import com.medithings.blueprint.support.*
import com.medithings.blueprint.ui.scanner.*
import org.tensorflow.lite.support.label.Category
import timber.log.Timber

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun UDSScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
) {
    val localPrefData = LocalPrefData(context = LocalContext.current)
    val scope = rememberCoroutineScope()
    val localContext = LocalContext.current
    val isAdmin by mainViewModel.adminFlow.collectAsState()
    val isDebugDialog = remember { mutableStateOf(false) }

    val transferLearningHelper = TransferLearningHelper(
        context = LocalContext.current,
        classifierListener = object : TransferLearningHelper.ClassifierListener {
            override fun onError(error: String) {
            }

            override fun onResults(results: List<Category>?, inferenceTime: Long) {
            }

            override fun onLossResults(lossNumber: Float) {
            }

            override fun onTrainSuccess() {
                localPrefData.setStoredMLData()
                mainViewModel.navigateTo(Main.route)
                //mainViewModel.connectDevice() // 얘 호출하면 알아서
            }

            override fun onResult(result: Float) {
            }
        }
    )

    val yInputDialog = remember {
        mutableStateOf(false)
    }

    val twoButtonDialog = remember {
        mutableStateOf(TwoButtonDialog(false))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FragmentTitle(text = stringResource(R.string.uds1))
            if (isAdmin) {
                PrimaryButton(
                    height = 30.dp,
                    width = 100.dp,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(R.string.setting10),
                    color = black65,
                    onClick = {
                        mainViewModel.navigateTo(Main.route)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        DialogSurface(
            bgColor = black12Disable
        ) {
            Text(
                lineHeight = 24.textDp,
                text = stringResource(R.string.uds2),
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
            text = stringResource(R.string.uds3),
            onClick = {
                if (mainViewModel.isProgressed.value == true) return@PrimaryButton
                yInputDialog.value = true
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = stringResource(R.string.uds4),
            onClick = {
                if (mainViewModel.isProgressed.value == true) return@PrimaryButton
                //viewModel.setTrainingState(TrainViewModel.TrainingState.TRAINING)
                transferLearningHelper.numThreads = 1
                transferLearningHelper.close()
                val trainCount = localPrefData.getTrainCount()
                val validationCount = localPrefData.getValidationCount()
                val epochCount = localPrefData.getEpochCount()
                val batchSize = localPrefData.getBatchSize()
                val patientCount = localPrefData.getPatientCount()

                transferLearningHelper.training(
                    mainViewModel.learnDataList,
                    trainCount,
                    validationCount,
                    epochCount,
                    batchSize,
                    patientCount,
                    mainViewModel.connectedDevice.value?.address ?: ""
                )
            }
        )

        if (isAdmin) {
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                color = black65,
                text = stringResource(id = R.string.setting11),
                onClick = {
                    isDebugDialog.value = true
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                color = black65,
                text = "DeBug View로 이동",
                onClick = {
                    mainViewModel.navigateTo(MLDebug.route)
                }
            )
        }

        OpenDialog(twoButtonDialog = twoButtonDialog.value) {
            twoButtonDialog.value = TwoButtonDialog(false)
        }

        if (isDebugDialog.value && isAdmin) {
            Dialog(onDismissRequest = {
                isDebugDialog.value = false
            }) {
                DebugValueDialog(
                    mainViewModel = mainViewModel,
                    localPrefData = localPrefData,
                    onBack = {
                        isDebugDialog.value = false
                    })
            }
        }

        if (yInputDialog.value) {
            var textValue by remember { mutableStateOf(TextFieldValue("")) }

            Dialog(onDismissRequest = {
                yInputDialog.value = false
            }) {
                DialogSurface {
                    BackHandler {
                        yInputDialog.value = false
                    }
                    Column {
                        VerticalMargin(dp = 32.dp)
                        dialogTitle(text = localContext.getString(R.string.uds5))
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
                            placeholder = { Text(localContext.getString(R.string.uds6)) },
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
                                    yInputDialog.value = false
                                    try {
                                        val inputVal = textValue.text.toInt()
                                        mainViewModel.showProgress()
                                        mainViewModel.udsSj(
                                            inputVal
                                        )
                                    } catch (e: Exception) {
                                        twoButtonDialog.value = TwoButtonDialog(
                                            true,
                                            "입력값이 잘못되었습니다.",
                                            "확인",
                                            onClickedConfirm = {
                                                twoButtonDialog.value = TwoButtonDialog(false)
                                            },
                                            confirmButton = "확인",
                                        )
                                        Timber.d(e)
                                    }
                                }, text = localContext.getString(R.string.uds7),
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