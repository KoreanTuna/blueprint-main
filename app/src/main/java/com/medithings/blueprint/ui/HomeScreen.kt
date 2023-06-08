package com.medithings.blueprint.ui

import TransferLearningHelper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.medithings.blueprint.support.*
import com.medithings.blueprint.ui.scanner.*
import org.tensorflow.lite.support.label.Category

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
) {
//    val viewModel = hiltViewModel<ScannerViewModel>()

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
            }

            override fun onResult(result: Float) {
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 10.dp, end = 12.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        mainViewModel.actionEvent.observe(LocalLifecycleOwner.current) {
            when (it.peekContent()) {
                is MainViewModel.ViewAction.FinishMainSj -> {
//                    mainViewModel.learnDataList.lastOrNull()?.let { learnData ->
//                        transferLearningHelper.mainInfer(learnData.sjData[0])
//                    }
                    // 측정버튼 눌렀으니 알람 다시 시작함
                    mainViewModel.cancelAlarm()
                    mainViewModel.startAlarm()
                }
                else -> Unit
            }
        }

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            FragmentTitle("모니터링 페이지", modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                height = 30.dp,
                width = 50.dp,
                modifier = Modifier.align(Alignment.CenterVertically),
                text = "설정",
                color = black65,
                onClick = {
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        DialogSurface(
            bgColor = white100,
            elevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Row(
                    modifier = Modifier
                        .background(bluePrimary)
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        lineHeight = 24.textDp,
                        text = "정상",
                        fontSize = 24.textDp,
                        fontWeight = FontWeight.Bold,
                        color = white100,
                        modifier = Modifier.padding(
                            20.dp
                        ),
                    )
                }

                Text(
                    lineHeight = 24.textDp,
                    text = "현재 잔료 상태는 정상범위로",
                    fontSize = 16.textDp,
                    fontWeight = FontWeight.Normal,
                    color = black100,
                    modifier = Modifier.padding(
                        start = 20.dp, end = 20.dp, top = 10.dp
                    ),
                )
                Text(
                    lineHeight = 24.textDp,
                    text = "자유로운 활동이 가능합니다.",
                    fontSize = 18.textDp,
                    fontWeight = FontWeight.Bold,
                    color = bluePrimary,
                    modifier = Modifier.padding(
                        start = 20.dp, end = 20.dp,
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 40.dp, end = 40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f)
                    ) {
                        Icon(
                            Icons.Default.PushPin,
                            "",
                            Modifier
                                .size(24.dp)
                                .align(Alignment.Center),
                            tint = bluePrimary,
                        )
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f)
                    ) {
//                        Icon(
//                            Icons.Default.PushPin,
//                            "",
//                            Modifier
//                                .size(24.dp)
//                                .align(Alignment.Center),
//                            tint = bluePrimary,
//                        )
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f)
                    ) {
//                        Icon(
//                            Icons.Default.PushPin,
//                            "",
//                            Modifier
//                                .size(24.dp)
//                                .align(Alignment.Center),
//                            tint = bluePrimary,
//                        )
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f)
                    ) {
                        Icon(
                            Icons.Default.PushPin,
                            "",
                            Modifier
                                .size(24.dp)
                                .align(Alignment.Center),
                            tint = yellowMiddle,
                        )
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f)
                    ) {
                        Icon(
                            Icons.Default.PushPin,
                            "",
                            Modifier
                                .size(24.dp)
                                .align(Alignment.Center),
                            tint = red100,
                        )
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f)
                    ) {
                        Icon(
                            Icons.Default.PushPin,
                            "",
                            Modifier
                                .size(24.dp)
                                .align(Alignment.Center),
                            tint = black65,
                        )
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f)
                    ) {
//                        Icon(
//                            Icons.Default.PushPin,
//                            "",
//                            Modifier
//                                .size(24.dp)
//                                .align(Alignment.Center),
//                            tint = black65,
//                        )
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f)
                    ) {
//                        Icon(
//                            Icons.Default.PushPin,
//                            "",
//                            Modifier
//                                .size(24.dp)
//                                .align(Alignment.Center),
//                            tint = black65,
//                        )
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // box row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 40.dp, end = 40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(1f)
                            .background(bluePrimary)
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(1f)
                            .background(bluePrimary)
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(1f)
                            .background(bluePrimary)
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(1f)
                            .background(yellowMiddle)
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(1f)
                            .background(red100)
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(1f)
                            .background(black65)
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(1f)
                            .background(black65)
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(1f)
                            .background(black65)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = "측정",
            onClick = {
                mainViewModel.sampleInfer()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = "도뇨완료기록",
            onClick = {
                // TODO 측정
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = "데이터 추가",
            onClick = {
                // TODO 학습 시작
            }
        )
    }
}

@Composable
fun HomeRow(
    label: String,
    contents: String,
    paddingTop: Dp = 0.dp,
    contentsColor: Color? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingTop)
            .height(60.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RowLabel(text = label)
            RowContents(text = contents, contentsColor)
        }
        DividerInBox()
    }
}

@Composable
fun RowLabel(
    text: String
) {
    Text(
        text = text,
        color = black65,
        fontSize = 13.textDp,
        modifier = Modifier.padding(start = 20.dp)
    )
}

@Composable
fun RowContents(
    text: String,
    contentsColor: Color? = null
) {
    Text(
        text = text,
        color = contentsColor ?: Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 17.textDp,
        textAlign = TextAlign.End,
        modifier = Modifier.padding(end = 20.dp)
    )
}

private val DefaultPadding = 12.dp