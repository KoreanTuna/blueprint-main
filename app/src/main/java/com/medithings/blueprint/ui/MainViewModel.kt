package com.medithings.blueprint.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.medithings.blueprint.Intro
import com.medithings.blueprint.RegistDevice
import com.medithings.blueprint.UDS
import com.medithings.blueprint.model.DiscoveredBluetoothDevice
import com.medithings.blueprint.model.LearnData
import com.medithings.blueprint.model.MacroEol
import com.medithings.blueprint.model.UARTData
import com.medithings.blueprint.model.UARTManager
import com.medithings.blueprint.model.parseWithNewLineChar
import com.medithings.blueprint.pref.LocalPrefData
import com.medithings.blueprint.support.Event
import com.medithings.blueprint.ui.scanner.ScanningState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.BuildConfig
import no.nordicsemi.android.ble.exception.DeviceDisconnectedException
import no.nordicsemi.android.ble.ktx.suspend
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : ViewModel() {

    private var manager: UARTManager = UARTManager(applicationContext, viewModelScope)
    val localPrefData = LocalPrefData(context = applicationContext)

    private val _actionEvent = MutableLiveData<ViewAction>()
    val actionLiveData: LiveData<ViewAction> = _actionEvent
    val actionEvent = Event.from(_actionEvent)

    val isProgressed = Transformations.map(_actionEvent) {
        it is ViewAction.ShowProgress
    }

    private val _connectedDevice = MutableLiveData<DiscoveredBluetoothDevice?>(null)
    val connectedDevice get() = _connectedDevice

    private val _devicesLiveData = MutableLiveData<List<DiscoveredBluetoothDevice>>(emptyList())
    val devicesLiveData get() = _devicesLiveData

    private lateinit var navController: NavHostController

    private val commandList = mutableListOf<Command>()

    fun navigateTo(destination: String, popupToInclusive: Boolean = false) {
        navController.navigate(destination) {
            if (popupToInclusive) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    fun connectDevice(devicesDiscovered: ScanningState.DevicesDiscovered) {
        _devicesLiveData.value = devicesDiscovered.devices
        _connectedDevice.value =
            devicesDiscovered.devices.firstOrNull { it.isBonded }

        connectDevice()
    }

    fun initDeviceOnly(devicesDiscovered: ScanningState.DevicesDiscovered) {
        _devicesLiveData.value = devicesDiscovered.devices
        _connectedDevice.value =
            devicesDiscovered.devices.firstOrNull { it.isBonded }

        navigateToIntro()
    }

    fun refreshDevice(devicesDiscovered: ScanningState.DevicesDiscovered) {
        _devicesLiveData.value = devicesDiscovered.devices
        _connectedDevice.value =
            devicesDiscovered.devices.firstOrNull { it.isBonded }

        connectDevice() // TODO 알아서 다른 화면으로 가게 만듬
    }

    fun navigateToIntro() {
        navigateTo(Intro.route)
    }

    fun unbondConnectDevice() {
        connectedDevice.value?.unBondBluetoothDevice()
        connectedDevice.value = null
        connectDevice()
    }

    fun connectDevice() {
        if (connectedDevice.value == null) {
            navigateTo(RegistDevice.route, true)
            //navigateTo(UDS.route, true)
            return
        }

        // connect 해야한다(bonding 된 녀석과)
        // 학습된 모델이 있는가?
        // return
        // 배터리 잔량 -> 60이상이면 학습 페이지로
        // 배터리 잔량 -> 30~60 일 경우 -> 배터리 부족 알림 -> to be continue
        // 배터리 잔량 -> 30미만 일 경우 -> 종료 팝업 띄워야함(viewAction으로 처리할까?)

        // 배터리 까지 다 통과하거나, 다음다음눌러서 온걸 의미함
        viewModelScope.launch {
            start()

            if (localPrefData.isStoredMLData()) {
                navigateTo(UDS.route)
                //navigateTo(Home.route)
                return@launch
            } else {
                navigateTo(UDS.route)
//                commandList.add(Command.TextComand("St", "Ready"))
//                commandList.add(Command.TextComand("status1", "Return"))
//                commandList.add(Command.TextComand("Sj", "Tj24"))
//                commandList.add(Command.TextComand("status0", "Return"))
//                commandList.add(Command.NavigateCommand(UDS.route))
//                startCommand()
                return@launch
            }
        }
    }

    private fun startCommand() {
        viewModelScope.launch {
            delay(60)
            when (val command = commandList[0]) {
                is Command.TextComand -> {
                    sendText(command.text)
                }

                is Command.NavigateCommand -> {
                    val destination = command.destination
                    commandList.removeAt(0)
                    navigateTo(destination)
                    if (commandList.isNotEmpty()) {
                        startCommand()
                    }
                }

                is Command.FinishCommand -> {
                    commandList.clear()
                    hideProgress()
                }

                is Command.MainSjFinishCommand -> {
                    commandList.clear()
                    hideProgress()
                    _actionEvent.value = ViewAction.FinishMainSj
                }

                is Command.CalBattery -> {
                    batteryResultValue.remove(batteryResultValue.min())
                    batteryResultValue.remove(batteryResultValue.max())

                    Timber.d("batteryResultValue cnt : ${batteryResultValue.size}")
                    val average = batteryResultValue.average() - 3300
                    Timber.d("batteryLiveData averagee : ${average}")

                    batteryResultValue.clear()
                    var result = (average / 6).toInt()
                    if (result > 100) {
                        result = 100
                    } else if (result < 0) {
                        result = 0
                    }
                    _batteryLiveData.value = result
                    Timber.d("batteryLiveData.value : ${batteryLiveData.value}")

                    commandList.removeAt(0)
                    if (commandList.isNotEmpty()) {
                        startCommand()
                    }
                }
            }
        }
    }

    fun initNavController(navController: NavHostController) {
        this.navController = navController
    }

    fun tryBondQr(qrValue: String) {
        createBond(qrValue) // qr logic 받아서 처리해야함
    }

    fun tryBondCustom(customString: String) {
        createBond(customString)
    }

    private fun createBond(pinString: String) {
        _devicesLiveData.value?.firstOrNull()?.let {
            localPrefData.setStoredPin(pinString)
            it.createBond(pinString)
        }

        showProgress()
    }

    private val _data = MutableLiveData<UARTData>()
    val uartData: LiveData<UARTData> get() = _data

    val learnDataList = mutableListOf<LearnData>()

    var autoRebootJob: Job? = null
    private fun catchSjData(data: UARTData) {
        if (data.displayMessages.find { it.text.contains("Tj24") } != null) {
            val returnList = mutableListOf<Float>()
            for (i in 1..24) {
                data.displayMessages.lastOrNull { it.text.contains("Tj$i") }?.text?.let { TjString ->
                    val tjValues = TjString.split(",").filter { it.contains("Tj").not() }.map {
                        it.toFloat()
                    }
                    returnList.addAll(tjValues)
                }
            }
            // FIXME 가라로 음수와 0을 없앰
            learnDataList.lastOrNull()?.sjData?.add(returnList.map {
                when {
                    it > 0 -> it
                    it < 0 -> -it
                    else -> 1.0f
                }.toFloat()
            })
        }
    }

    private var startFlag = false

    private val batteryResultValue = mutableListOf<Int>()

    private val _batteryLiveData = MutableLiveData(0)
    val batteryLiveData: LiveData<Int> = _batteryLiveData

    suspend fun start() {
        if (_connectedDevice.value?.device == null) return

        if (startFlag.not()) {
            startFlag = true

            manager.data.onEach { uartData -> // uart Data 전송
                if (uartData.messages.isNotEmpty()) {
                    Timber.d("medithings ===  uartData : ${uartData.messages.last().text}")
                }
                _data.value = uartData
                if (commandList.isNotEmpty() && commandList[0] is Command.TextComand) {
                    if (BuildConfig.DEBUG) {
                        autoRebootJob?.cancel()
                        autoRebootJob = viewModelScope.launch {
                            delay(5000)
                            sendText("Ss") // reboot text
                        }
                    }

                    (commandList[0] as Command.TextComand).let { command ->
                        if (uartData.displayMessages.last().text.contains(command.completeStr)) {
                            autoRebootJob?.cancel() // complete string 왔으니 취소
                            autoRebootJob = null
                            Timber.d("medithings ===  complete command : $command")
                            when (command.text) {
                                "agc" -> {
                                    localPrefData.setAgcData(uartData.displayMessages.filter {
                                        it.text.contains(
                                            "Tagc"
                                        )
                                    }.let { tagcs ->
                                        tagcs.fold(tagcs.first().text) { next, total ->
                                            next + total.text
                                        }
                                    })
                                }

                                "Sj" -> {
                                    catchSjData(uartData)
                                }

                                "Sn" -> {
                                    Timber.d(
                                        "medithings ===  Sn : ${
                                            uartData.displayMessages.last().text.substring(
                                                2
                                            )
                                        }"
                                    )
                                    batteryResultValue.add(
                                        uartData.displayMessages.last().text.substring(
                                            2
                                        ).trim().toInt()
                                    )
                                }

                                else -> {}
                            }
                            commandList.removeAt(0)
                            if (commandList.isNotEmpty()) {
                                startCommand()
                            }
                        }
                    }
                }
            }.launchIn(viewModelScope)

            manager.start(_connectedDevice.value!!)
        }
    }

    fun sendText(text: String, newLineChar: MacroEol = MacroEol.LF) {
        viewModelScope.launch {
            manager.send(text.parseWithNewLineChar(newLineChar))
        }
    }

    private suspend fun UARTManager.start(device: DiscoveredBluetoothDevice) {
        try {
            connect(device.device)
                .useAutoConnect(false)
                .retry(3, 100)
                .suspend()
        } catch (e: Exception) {
            if (e is DeviceDisconnectedException) {
                _actionEvent.value = ViewAction.Disconnected
            }
            e.printStackTrace()
        }
    }

    // for demo sampleUds
    fun sampleUds() {
        showProgress()
        commandList.add(Command.TextComand("St", "Ready"))
        commandList.add(Command.TextComand("status1", "Return"))
        commandList.add(Command.TextComand("Sc1", "Tc"))
        commandList.add(Command.TextComand("Sc5", "Tc"))
        commandList.add(Command.TextComand("Sc7", "Tc"))
        commandList.add(Command.TextComand("Sc99", "Tc"))
        commandList.add(Command.TextComand("status0", "Return"))
        startCommand()
        viewModelScope.launch {
            delay(3000)
            hideProgress()
            _actionEvent.value = ViewAction.ShowToast("측정이 완료되었습니다.")
        }
    }

    fun sampleTrain() {
        showProgress()
        viewModelScope.launch {
            delay(1000)
            hideProgress()
            _actionEvent.value = ViewAction.ShowToast("학습이 완료되었습니다.")
            delay(100)
            _actionEvent.value = ViewAction.ToMain
        }
    }

    fun sampleInfer() {
        showProgress()
        commandList.add(Command.TextComand("St", "Ready"))
        commandList.add(Command.TextComand("status1", "Return"))
        commandList.add(Command.TextComand("Sc1", "Tc"))
        commandList.add(Command.TextComand("Sc5", "Tc"))
        commandList.add(Command.TextComand("Sc7", "Tc"))
        commandList.add(Command.TextComand("Sc99", "Tc"))
        commandList.add(Command.TextComand("status0", "Return"))
        startCommand()
        viewModelScope.launch {
            delay(3000)
            hideProgress()
            commandList.add(Command.MainSjFinishCommand)
            startCommand()
            _actionEvent.value = ViewAction.ShowToast("측정이 완료되었습니다.")
        }
    }

    fun udsSj(yValue: Int) {
        val executeCount =
            localPrefData.getTrainCount() + localPrefData.getTestCount() + localPrefData.getValidationCount()

        learnDataList.add(LearnData(yValue.toFloat()))
        //showProgress()
        commandList.add(Command.TextComand("St", "Ready"))
        commandList.add(Command.TextComand("status1", "Return"))
        if (localPrefData.getAgcData().isNullOrBlank()) {
            commandList.add(Command.TextComand("agc", "Tagc-B20"))
        }
        for (i in 0 until executeCount) {
            commandList.add(Command.TextComand("Sj", "Tj24"))
        }
        commandList.add(Command.TextComand("status0", "Return"))
        commandList.add(Command.FinishCommand)
        startCommand()
    }

    // 실전 추론(메인에서 사용)
    fun mainSj() {
        learnDataList.clear()
        learnDataList.add(LearnData(0.0f)) // y값은 추후에 추론 후 그걸 다시 써야함
        showProgress()
        commandList.add(Command.TextComand("St", "Ready"))
        commandList.add(Command.TextComand("status1", "Return"))
        for (i in 0 until localPrefData.getMainInferCount()) {
            commandList.add(Command.TextComand("Sj", "Tj24"))
        }
        commandList.add(Command.TextComand("status0", "Return"))
        commandList.add(Command.MainSjFinishCommand)
        startCommand()
    }

    fun showProgress() {
        _actionEvent.value = ViewAction.ShowProgress
    }

    fun hideProgress() {
        _actionEvent.value = ViewAction.HideProgress
    }

    fun startAlarm() {
        _actionEvent.value = ViewAction.StartAlarm
    }

    fun cancelAlarm() {
        _actionEvent.value = ViewAction.CancelAlarm
    }

    fun showToast(message: String) {
        _actionEvent.value = ViewAction.ShowToast(message)
    }

    fun navigateUp() {
        _actionEvent.value = ViewAction.NavigateUp
    }

    fun getBattery() {
        if (_lastCalc.value == 0L) {
            _lastCalc.value = System.currentTimeMillis()
        }

        showProgress()
        commandList.add(Command.TextComand("Sn", "Tn"))
        commandList.add(Command.TextComand("Sn", "Tn"))
        commandList.add(Command.TextComand("Sn", "Tn"))
        commandList.add(Command.TextComand("Sn", "Tn"))
        commandList.add(Command.TextComand("Sn", "Tn"))
        commandList.add(Command.TextComand("Sn", "Tn"))
        commandList.add(Command.TextComand("Sn", "Tn"))
        commandList.add(Command.CalBattery)
        commandList.add(Command.FinishCommand)
        startCommand()
    }

    private val _bladderValue = MutableLiveData<List<Int>>(mutableListOf())
    val bladderValue: LiveData<List<Int>> = Transformations.map(_bladderValue) {
        it.reversed().let { reversedList ->
            if (reversedList.size > 2) {
                reversedList.subList(0, 2)
            } else {
                reversedList
            }
        }
    }

    private val _calsList = MutableLiveData<List<Long>>(mutableListOf())
    val calsList: LiveData<List<Long>> = Transformations.map(_calsList) {
        it.reversed().let { reversedList ->
            if (reversedList.size > 2) {
                reversedList.subList(0, 2)
            } else {
                reversedList
            }
        }
    }

    private val _currentBladder = MutableLiveData<Int>(0)
    val currentBladder: LiveData<Int?> = _currentBladder

    private val _lastCalc = MutableLiveData(0L)
    val lastCalc: LiveData<Long> = _lastCalc

    fun mainInferResult(result: Int) {
        _currentBladder.value?.let {
            _bladderValue.value = _bladderValue.value?.plus(it)
        }
        _currentBladder.value = result
        _lastCalc.value?.let {
            _calsList.value = _calsList.value?.plus(it)
        }
        _lastCalc.value = System.currentTimeMillis()
    }

    fun getAlarmTerm() = localPrefData.getAlarmTerm()

    private val _adminFlow: MutableStateFlow<Boolean> = MutableStateFlow(localPrefData.isAdmin())
    val adminFlow: StateFlow<Boolean> = _adminFlow.asStateFlow()

    fun setAdmin(pwKey: String) {
        if (pwKey == ADMIN_KEY) {
            _adminFlow.value = true
            localPrefData.setIsAdmin(true)
        } else {
            _actionEvent.value = ViewAction.ShowToast("관리자 비밀번호가 틀렸습니다.")
        }
    }

    fun exitAdminMode() {
        _adminFlow.value = false
        localPrefData.setIsAdmin(false)
        _actionEvent.value = ViewAction.ShowToast("관리자 모드를 종료합니다.")
    }

    fun actionUnit() {
        _actionEvent.value = ViewAction.Unit
    }

    companion object {
        const val COMMAND_BASE_READY = "St"

        //const val ADMIN_KEY = "1!2@3#4$5%medi"
        const val ADMIN_KEY = "1"
    }

    sealed class ViewAction {
        object Unit : ViewAction()
        object NotFoundDevice : ViewAction()
        object BondFailed : ViewAction()

        object ShowProgress : ViewAction()

        object HideProgress : ViewAction()

        object FinishMainSj : ViewAction()

        object StartAlarm : ViewAction()

        object CancelAlarm : ViewAction()

        object Disconnected : ViewAction()

        data class ShowToast(val message: String) : ViewAction()
        object ToMain : ViewAction()

        object NavigateUp : ViewAction()
    }

    sealed class Command {
        data class TextComand(val text: String, val completeStr: String = "") : Command()
        data class NavigateCommand(val destination: String) : Command()
        object FinishCommand : Command()
        object MainSjFinishCommand : Command()
        object CalBattery : Command()
    }

    data class InferData(
        val epochMillis: Long,
        val yValue: Float,
        val inferLevel: Int
    )

//    fun getInferData(yValue: Float) = InferData(
//        //epochMillis = LocalDate.now().toEpochDay()
//    )
}