package com.medithings.blueprint

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.medithings.blueprint.Constant.Companion.NOTIFICATION_ID
import com.medithings.blueprint.pref.LocalPrefData
import com.medithings.blueprint.support.TwoButtonDialog
import com.medithings.blueprint.support.bluePrimary
import com.medithings.blueprint.support.observeEvent
import com.medithings.blueprint.ui.MainViewModel
import com.medithings.blueprint.ui.OpenDialog
import com.medithings.blueprint.ui.component.TopRow
import com.medithings.blueprint.ui.theme.BlueprintTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.UnsupportedEncodingException
import kotlin.system.exitProcess


@Composable
fun getActivity() = LocalContext.current as ComponentActivity

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        const val CLICK_ALARM = "clickAlarm"
    }

    private val mainViewModel: MainViewModel by viewModels()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) return

        if (intent.getBooleanExtra(CLICK_ALARM, false)) {
            // 알람 클릭함, 다시 알람 시작
            mainViewModel.cancelAlarm()
            mainViewModel.startAlarm()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            this, NOTIFICATION_ID, Intent(this, MyReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {

                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                }
            })
            .setDeniedMessage("권한을 거부하면 앱을 사용할 수 없습니다.")
            .setPermissions(POST_NOTIFICATIONS)
            .check()

        setContent {
            BlueprintTheme(
                dynamicColor = false,
                darkTheme = true,
            ) {
                //var currentScreen: RallyDestination by remember { mutableStateOf(Overview) }
                val navController = rememberNavController()
                mainViewModel.initNavController(navController)

                val currentBackStack by navController.currentBackStackEntryAsState()

                // Fetch your currentDestination:
                val currentDestination = currentBackStack?.destination

                val showProgress = remember {
                    mutableStateOf(false)
                }

                val twoButtonDialog = remember {
                    mutableStateOf(TwoButtonDialog(false))
                }

                Timber.d("medithings ===  route: ${currentDestination?.route}")
                val currentScreen =
                    BluePrintScreens.find { it.route == currentDestination?.route } ?: Scanner

                mainViewModel.actionEvent.observeEvent(this) {
                    when (it) {
                        is MainViewModel.ViewAction.NotFoundDevice -> {
                            // DO_NOTHING
                            // TODO show NotFound? or 이벤트만? 추후 추가
                        }
                        is MainViewModel.ViewAction.BondFailed -> {

                        }
                        is MainViewModel.ViewAction.ShowProgress -> {
                            showProgress.value = true
                        }
                        is MainViewModel.ViewAction.HideProgress -> {
                            showProgress.value = false
                        }
                        is MainViewModel.ViewAction.Disconnected -> {
                            twoButtonDialog.value = TwoButtonDialog(
                                true,
                                title = "연결 실패",
                                description = "연결에 실패했습니다. 본딩을 해제하고 다시 새로고침하시겠습니까?",
                                onClickedCancel = {
                                    exitProcess(0)
                                },
                                cancelButton = "앱 종료",
                                onClickedConfirm = {
                                    twoButtonDialog.value = TwoButtonDialog(false)
                                    mainViewModel.unbondConnectDevice()
                                },
                                confirmButton = "본딩해제",
                            )
                        }
                        is MainViewModel.ViewAction.StartAlarm -> {
                            Timber.d("알람 생성")
                            val repeatInterval = mainViewModel.getAlarmTerm() * 60 * 1000
                            val triggerTime = (SystemClock.elapsedRealtime()
                                    + repeatInterval)
                            alarmManager.setInexactRepeating(
                                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                triggerTime, repeatInterval.toLong(),
                                pendingIntent
                            ) // setInexactRepeating : 반복 알림
                            //"${repeatInterval/60000}분 마다 알림이 발생합니다."
                        }
                        is MainViewModel.ViewAction.CancelAlarm -> {
                            alarmManager.cancel(pendingIntent)
                        }
                        is MainViewModel.ViewAction.ToMain -> {
                            mainViewModel.navigateTo(Home.route)
                        }
                        is MainViewModel.ViewAction.ShowToast -> { // show toast
                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        }
                        is MainViewModel.ViewAction.NavigateUp -> {
                            navController.navigateUp()
                        }
                        else -> Unit
                    }
                }

                // A surface container using the 'background' color from the theme
                Scaffold(
                    topBar = {
                        if (currentScreen.topType !is TopType.NONE) {
                            TopRow(currentScreen = currentScreen, navController = navController)
                        }
                    },
                ) { innerPadding ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        NavHost(
                            navController = navController,
                            startDestination = Splash.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(route = Start.route) {
                                Start.screen()
                            }
                            composable(route = Home.route) {
                                Home.screen()
                            }
                            composable(route = Train.route) {
                                Train.screen()
                            }
                            composable(route = Scanner.route) {
                                Scanner.screen()
                            }
                            composable(route = Intro.route) {
                                Intro.screen()
                            }
                            composable(route = RegistDevice.route) {
                                RegistDevice.screen()
                            }
                            composable(route = MLDebug.route) {
                                MLDebug.screen()
                            }
                            composable(route = UDS.route) {
                                UDS.screen()
                            }
                            composable(route = TwoPoint.route) {
                                TwoPoint.screen()
                            }
                            composable(route = Splash.route) {
                                Splash.screen()
                            }
                            composable(route = Main.route) {
                                Main.screen()
                            }
                            composable(route = Setting.route) {
                                Setting.screen()
                            }
                            composable(route = BladderLevSetting.route) {
                                BladderLevSetting.screen()
                            }
                            composable(route = AlarmTermSetting.route) {
                                AlarmTermSetting.screen()
                            }
                        }

                        OpenDialog(twoButtonDialog = twoButtonDialog.value) {
                            twoButtonDialog.value = TwoButtonDialog(false)
                        }

                        CircularIndeterminateProgressBar(
                            modifier = Modifier.align(Alignment.Center),
                            isDisplayed = showProgress.value
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun CircularIndeterminateProgressBar(
    isDisplayed: Boolean,
    modifier: Modifier,
) {
    if (isDisplayed) {
        CircularProgressIndicator(
            modifier = modifier
                .padding(8.dp)
                .width(200.dp)
                .height(200.dp),
            color = bluePrimary,
            strokeWidth = 10.dp
        )
    }
}

class BluetoothPairingRequest : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (BluetoothDevice.ACTION_PAIRING_REQUEST == action) {
            // convert broadcast intent into activity intent (same action string)
            val localPrefData = LocalPrefData(context = context)

            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            val type =
                intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR)
            val pairingIntent = Intent()
            pairingIntent.setClass(context, MainActivity::class.java)
            pairingIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, device)
            pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, type)
            pairingIntent.action = BluetoothDevice.ACTION_PAIRING_REQUEST
            pairingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (device != null) {
                try {
                    localPrefData.getStoredPin()?.let {
                        device.setPin(it.toByteArray(charset("UTF-8")))
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
            context.startActivity(pairingIntent)
        }
    }
}
//        val conditions = CustomModelDownloadConditions.Builder()
//            .requireWifi()
//            .build()
//        FirebaseModelDownloader.getInstance()
//            .getModel("sample-3", DownloadType.LOCAL_MODEL, conditions)
//            .addOnCompleteListener {
//                Log.d("medithings === ", it.result.size.toString() + ": size")
//                //val interpreter = Interpreter(it.result as File)
//            }