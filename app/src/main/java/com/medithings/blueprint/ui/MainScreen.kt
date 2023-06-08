package com.medithings.blueprint.ui

import TransferLearningHelper
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.medithings.blueprint.R
import com.medithings.blueprint.Setting
import com.medithings.blueprint.UDS
import com.medithings.blueprint.pref.LocalPrefData
import com.medithings.blueprint.support.*
import com.medithings.blueprint.ui.scanner.*
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.label.Category
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val scope = rememberCoroutineScope()


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
                Timber.d("medilight: infer")
                scope.launch {
                    mainViewModel.mainInferResult(result.let {
                        when {
                            it > 0 -> it
                            it < 0 -> -it
                            else -> 1.0f
                        }.toInt()
                    })
                }
            }
        }
    )

    val isAdmin by mainViewModel.adminFlow.collectAsState()

    val mainAction by mainViewModel.actionLiveData.observeAsState()

    if (mainAction is MainViewModel.ViewAction.FinishMainSj) {
        mainViewModel.actionUnit()
        transferLearningHelper.numThreads = 1
        transferLearningHelper.close()
        transferLearningHelper.infer(
            mainViewModel.learnDataList.last(),
            mainViewModel.connectedDevice.value?.address ?: ""
        )
    }

    val localPrefData = LocalPrefData(context = LocalContext.current)
    val fireLev = localPrefData.getFireLev()
    val localContext = LocalContext.current
    val screenLev = remember {
        mutableStateOf(1)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->

            Timber.d("MainScreen: $event")
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    mainViewModel.getBattery()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val batteryScore by mainViewModel.batteryLiveData.observeAsState()

    val lastCalc by mainViewModel.lastCalc.observeAsState()
    val lastCalcTime = if (lastCalc == 0L) {
        System.currentTimeMillis()
    } else {
        lastCalc ?: System.currentTimeMillis()
    }

    val bladder by mainViewModel.bladderValue.observeAsState()
    val currentBladder by mainViewModel.currentBladder.observeAsState()
    val calsList by mainViewModel.calsList.observeAsState()

    // TODO 추후 MAX값으로 변경 -> https://github.com/jm-lim/blueprint/issues/12#issuecomment-1545389552
    val maxBladder = 400.0f

    val currentLev = when (currentBladder) {
        in 0..(maxBladder * 0.125).toInt() -> 1
        in (maxBladder * 0.125).toInt()..(maxBladder * 0.25).toInt() -> 2
        in (maxBladder * 0.25).toInt()..(maxBladder * 0.375).toInt() -> 3
        in (maxBladder * 0.375).toInt()..(maxBladder * 0.5).toInt() -> 4
        in (maxBladder * 0.5).toInt()..(maxBladder * 0.625).toInt() -> 5
        in (maxBladder * 0.625).toInt()..(maxBladder * 0.75).toInt() -> 6
        in (maxBladder * 0.75).toInt()..(maxBladder * 0.875).toInt() -> 7
        else -> 8
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 10.dp, end = 10.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        BackHandler {
            exitProcess(0)
        }

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.medilight_logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(30.dp)
                        .width(150.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.connect),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        lineHeight = 24.textDp,
                        text = "$batteryScore %",
                        fontSize = 24.textDp,
                        fontWeight = FontWeight.Medium,
                        color = black100,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                height = 30.dp,
                width = 100.dp,
                modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(R.string.settings),
                color = black65,
                onClick = {
                    mainViewModel.navigateTo(Setting.route)
                }
            )
        }
        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 30.dp),
                lineHeight = 24.textDp,
                text = stringResource(R.string.smart_care),
                fontSize = 24.textDp,
                fontWeight = FontWeight.Normal,
                color = black100,
            )

            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 50.dp),
                lineHeight = 24.textDp,
                text = stringResource(R.string.aram_kim),
                fontSize = 20.textDp,
                fontWeight = FontWeight.Normal,
                color = black100,
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
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val today =
                    SimpleDateFormat(stringResource(R.string.date_format)).format(Calendar.getInstance().time)

                Row(
                    modifier = Modifier
                        .background(
                            if (fireLev - currentLev > 1) {
                                bluePrimary
                            } else if (fireLev - currentLev == 1) {
                                yellowMiddle
                            } else {
                                red200
                            }
                        )
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 16.dp, bottom = 16.dp, start = 12.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(
                            id = if (fireLev - currentLev > 1) {
                                R.drawable.thumbup_white
                            } else if (fireLev - currentLev == 1) {
                                R.drawable.sand_white
                            } else {
                                R.drawable.caution_white
                            }
                        ),
                        contentDescription = null,
                        //contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        lineHeight = 24.textDp,
                        text = if (fireLev - currentLev > 1) {
                            stringResource(R.string.normal)
                        } else if (fireLev - currentLev == 1) {
                            stringResource(R.string.prepare)
                        } else {
                            stringResource(R.string.user_alert)
                        },
                        fontSize = 24.textDp,
                        fontWeight = FontWeight.Bold,
                        color = white100,
                    )
                    Spacer(modifier = Modifier.weight(1.0f))
                    Text(
                        lineHeight = 18.textDp,
                        text = today,
                        fontSize = 18.textDp,
                        fontWeight = FontWeight.Normal,
                        color = white100,
                    )
                }

                Text(
                    lineHeight = 18.textDp,
                    text = if (fireLev - currentLev > 1) {
                        stringResource(R.string.desc1)
                    } else if (fireLev - currentLev == 1) {
                        stringResource(R.string.desc2)
                    } else {
                        stringResource(R.string.desc3)
                    },
                    fontSize = 18.textDp,
                    fontWeight = FontWeight.Normal,
                    color = black100,
                    modifier = Modifier.padding(
                        start = 20.dp, end = 20.dp, top = 10.dp
                    ),
                )
                Text(
                    lineHeight = 24.textDp,
                    text = if (fireLev - currentLev > 1) {
                        stringResource(R.string.desc4)
                    } else if (fireLev - currentLev == 1) {
                        stringResource(R.string.desc5)
                    } else {
                        stringResource(R.string.desc6)
                    },
                    fontSize = 24.textDp,
                    fontWeight = FontWeight.Bold,
                    color = if (fireLev - currentLev > 1) {
                        bluePrimary
                    } else if (fireLev - currentLev == 1) {
                        yellowMiddle
                    } else {
                        red200
                    },
                    modifier = Modifier.padding(
                        start = 20.dp, end = 20.dp,
                    ),
                )

                Spacer(modifier = Modifier.height(2.dp))


                val configuration = LocalConfiguration.current

                val screenHeight = configuration.screenHeightDp.dp
                val screenWidth = configuration.screenWidthDp.dp

                val leftPadding = (screenWidth.value - 335.0f) / 2

                Timber.d("medi leftPadding : (${screenWidth.value}) $leftPadding")

                val shiftFlag = 297.0f / maxBladder

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                    //.padding(start = leftPadding.dp, end = leftPadding.dp)
                ) {
                    bladder?.forEach {
                        val xValue = it * shiftFlag

                        Timber.d("xValue : $xValue")
                        Image(
                            painter = if (it < maxBladder * 0.375) {
                                painterResource(id = R.drawable.normal_past)
                            } else if (it < maxBladder * 0.5) {
                                painterResource(id = R.drawable.ready_past)
                            } else {
                                painterResource(id = R.drawable.fire_past)
                            },
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = xValue.dp)
                                .height(23.dp)
                                .width(28.dp)
                                .align(Alignment.BottomStart)
                        )
                    }

                    currentBladder?.let { currentBladder ->

                        //val shiftFlagC = 291.0f / maxBladder
                        var start = (currentBladder * shiftFlag) - 20
                        if (start < 0) start = 0f
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(start = start.dp)
                                //.background(white100)
                                .align(Alignment.BottomStart)
                        ) {
                            Image(
                                painter = if (currentBladder < maxBladder * 0.375) {
                                    painterResource(id = R.drawable.normal)
                                } else if (currentBladder < maxBladder * 0.5) {
                                    painterResource(id = R.drawable.ready)
                                } else {
                                    painterResource(id = R.drawable.fire)
                                },
                                contentDescription = null,
                                modifier = Modifier
                                    .height(69.dp)
                                    .width(80.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .align(alignment = Alignment.TopCenter),
                                lineHeight = 12.textDp,
                                text = "Lv",
                                fontSize = 12.textDp,
                                fontWeight = FontWeight.Normal,
                                color = white100,
                            )
                            Text(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .align(alignment = Alignment.Center),
                                lineHeight = 20.textDp,
                                text = when (currentBladder) {
                                    in 0..(maxBladder * 0.125).toInt() -> "1"
                                    in (maxBladder * 0.125).toInt()..(maxBladder * 0.25).toInt() -> "2"
                                    in (maxBladder * 0.25).toInt()..(maxBladder * 0.375).toInt() -> "3"
                                    in (maxBladder * 0.375).toInt()..(maxBladder * 0.5).toInt() -> "4"
                                    in (maxBladder * 0.5).toInt()..(maxBladder * 0.625).toInt() -> "5"
                                    in (maxBladder * 0.625).toInt()..(maxBladder * 0.75).toInt() -> "6"
                                    in (maxBladder * 0.75).toInt()..(maxBladder * 0.875).toInt() -> "7"
                                    else -> "8"
                                },
                                fontSize = 20.textDp,
                                fontWeight = FontWeight.Bold,
                                color = white100,
                            )
                        }
                    }
                }
                // box row
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(38.dp)
                                .background(bluePrimary)
                        )
                        if (currentLev == 1) {
                            DiffTextView(lastCalcTime)
                        }
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Column(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(38.dp)
                                .background(bluePrimary)
                        )
                        if (currentLev == 2) {
                            DiffTextView(lastCalcTime)
                        }
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Column(
                        modifier = Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(38.dp)
                                .background(bluePrimary)
                        )
                        if (currentLev == 3) {
                            DiffTextView(lastCalcTime)
                        }
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Column(
                        modifier = Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(38.dp)
                                .background(yellowMiddle)
                        )
                        if (currentLev == 4) {
                            DiffTextView(lastCalcTime)
                        }
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Column(
                        modifier = Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(38.dp)
                                .background(red200)
                        )
                        if (currentLev == 5) {
                            DiffTextView(lastCalcTime)
                        }
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Column(
                        modifier = Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(38.dp)
                                .background(red200)
                        )
                        if (currentLev == 6) {
                            DiffTextView(lastCalcTime)
                        }
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Column(
                        modifier = Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(38.dp)
                                .background(red200)
                        )
                        if (currentLev == 7) {
                            DiffTextView(lastCalcTime)
                        }
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Column(
                        modifier = Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(38.dp)
                                .background(red200)
                        )
                        if (currentLev == 8) {
                            DiffTextView(lastCalcTime)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        DialogSurface(
            bgColor = demoDialog
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(10.dp)
            ) {

                // TODO 자세히 버튼 추후 develop 예정임
//                PrimaryButton(
//                    modifier = Modifier
//                        .height(50.dp)
//                        .align(Alignment.TopEnd),
//                    //height = 30.dp,
//                    width = 100.dp,
//                    text = stringResource(R.string.detail1),
//                    color = black65,
//                    onClick = {
//                    }
//                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        lineHeight = 24.textDp,
                        text = stringResource(R.string.detail2),
                        fontSize = 20.textDp,
                        fontWeight = FontWeight.Bold,
                        color = black100,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (lastCalcTime != 0L && currentBladder != null) {
                        Text(
                            lineHeight = 24.textDp,
                            text = displayDesc(
                                timeTick = lastCalcTime,
                                bladder = currentBladder!!,
                                maxValue = maxBladder,
                                fireLev = fireLev,
                                context = localContext
                            ),
                            fontSize = 20.textDp,
                            fontWeight = FontWeight.Bold,
                            color = black100,
                        )
                    }

                    calsList?.forEachIndexed { index, it ->
                        Text(
                            lineHeight = 24.textDp,
                            text = displayDesc(
                                timeTick = it,
                                bladder = bladder?.get(index) ?: 0,
                                maxValue = maxBladder,
                                fireLev = fireLev,
                                context = localContext
                            ),
                            fontSize = 20.textDp,
                            fontWeight = FontWeight.Normal,
                            color = black100,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            modifier = Modifier.padding(start = 32.dp, end = 32.dp),
            text = stringResource(R.string.bottombtn1), // 측정버튼
            onClick = {
                mainViewModel.mainSj()
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton(
            modifier = Modifier.padding(start = 32.dp, end = 32.dp),
            text = stringResource(R.string.bottombtn2),
            onClick = {
                // TODO 측정
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (isAdmin) {
            PrimaryButton(
                color = black65,
                modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                text = stringResource(R.string.bottombtn3),
                onClick = {
                    mainViewModel.navigateTo(UDS.route)
                }
            )
        }
    }
}

fun displayDesc(
    timeTick: Long,
    bladder: Int,
    maxValue: Float,
    fireLev: Int,
    context: Context
): String {
    Timber.d("displayDesc timeTick: $timeTick, bladder: $bladder, maxValue: $maxValue, fireLev: $fireLev")
    val lev = when (bladder) {
        in 0..(maxValue * 0.125).toInt() -> 1
        in (maxValue * 0.125).toInt()..(maxValue * 0.25).toInt() -> 2
        in (maxValue * 0.25).toInt()..(maxValue * 0.375).toInt() -> 3
        in (maxValue * 0.375).toInt()..(maxValue * 0.5).toInt() -> 4
        in (maxValue * 0.5).toInt()..(maxValue * 0.625).toInt() -> 5
        in (maxValue * 0.625).toInt()..(maxValue * 0.75).toInt() -> 6
        in (maxValue * 0.75).toInt()..(maxValue * 0.875).toInt() -> 7
        else -> 8
    }

    val descString = if (fireLev - lev > 1) {
        context.getString(R.string.detail3)
    } else if (fireLev - lev == 1) {
        context.getString(R.string.detail4)
    } else {
        context.getString(R.string.detail5)
    }
    return "${SimpleDateFormat("HH:mm").format(timeTick)} : Lv.$lev $descString"
}

@Composable
fun DiffTextView(lastCalcTime: Long) {
    Text(
        lineHeight = 10.textDp,
//        text = when {
//            min < 60 -> String.format(stringResource(R.string.desc7), min)
//            min < 60 * 24 -> String.format(stringResource(R.string.desc8), min * 60)
//            else -> String.format(stringResource(R.string.desc9), min * 60 * 24)
//        },
        text = SimpleDateFormat("HH:mm").format(lastCalcTime),
        fontSize = 10.textDp,
        fontWeight = FontWeight.Normal,
        color = black100,
    )
}