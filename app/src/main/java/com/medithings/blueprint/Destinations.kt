/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.medithings.blueprint

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.medithings.blueprint.ui.*
import com.medithings.blueprint.ui.scanner.ScannerScreen
import com.medithings.blueprint.ui.train.TrainScreen

/**
 * Contract for information needed on every Rally navigation destination
 */
interface BluePrintDestination {
    val topType: TopType
    val route: String
    val screen: @Composable () -> Unit
    val screenTitle: String
}

object Start : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "Start"
    override val screen: @Composable () -> Unit = { StartScreen() }
    override val screenTitle: String = "Splash"
}

object Home : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "home"
    override val screen: @Composable () -> Unit = { HomeScreen() }
    override val screenTitle: String = "홈"
}

object Splash : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "SplashScreen"
    override val screen: @Composable () -> Unit = { SplashScreen() }
    override val screenTitle: String = "스플래시"
}

object Main : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "Demo"
    override val screen: @Composable () -> Unit = { MainScreen() }
    override val screenTitle: String = "Demo"
}

object Setting : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "Setting"
    override val screen: @Composable () -> Unit = { SettingScreen() }
    override val screenTitle: String = "Setting"
}

object BladderLevSetting : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "BladderLevSetting"
    override val screen: @Composable () -> Unit = { BladderLevSettingScreen() }
    override val screenTitle: String = "배뇨량설정"
}

object AlarmTermSetting : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "AlarmTermSetting"
    override val screen: @Composable () -> Unit = { AlarmTermSettingScreen() }
    override val screenTitle: String = "알람주기설정"
}

object MLDebug : BluePrintDestination {
    override val topType = TopType.Icon(Icons.Filled.Home)
    override val route = "MLDebug"
    override val screen: @Composable () -> Unit = { MLDebugScreen() }
    override val screenTitle: String = "관리자모드-UARTDebug"
}

object RegistDevice : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "registDevice"
    override val screen: @Composable () -> Unit = { RegistScreen() }
    override val screenTitle: String = "기기등록"
}

object UDS : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "UDS"
    override val screen: @Composable () -> Unit = { UDSScreen() }
    override val screenTitle: String = "UDS"
}

object TwoPoint : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "twoPoint"
    override val screen: @Composable () -> Unit = { TwoPointScreen() }
    override val screenTitle: String = "TwoPoint"
}

object Intro : BluePrintDestination {
    override val topType = TopType.NONE
    override val route = "intro"
    override val screen: @Composable () -> Unit = { IntroScreen() }
    override val screenTitle: String = "Intro"
}

object Train : BluePrintDestination {
    override val topType = TopType.BACK
    override val route = "train"
    override val screen: @Composable () -> Unit = { TrainScreen() }
    override val screenTitle: String = "모델훈련"
}

object Scanner : BluePrintDestination {
    override val topType = TopType.BACK
    override val route = "scanner"
    override val screen: @Composable () -> Unit = { ScannerScreen() }
    override val screenTitle: String = "스캔"
}

sealed class TopType {
    data class Icon(val icon: ImageVector) : TopType()
    object BACK : TopType()
    object NONE : TopType()
}

// Screens to be displayed in the top RallyTabRow
val BluePrintScreens =
    listOf(
        Train,
        Start,
        Home,
        Intro,
        Scanner,
        RegistDevice,
        MLDebug,
        UDS,
        TwoPoint,
        Splash,
        Main,
        Setting,
        BladderLevSetting,
        AlarmTermSetting,
    )
