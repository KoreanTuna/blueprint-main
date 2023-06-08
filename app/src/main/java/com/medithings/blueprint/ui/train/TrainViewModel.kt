package com.medithings.blueprint.ui.train

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class TrainViewModel @Inject constructor() : ViewModel() {
    init {
        val settings: ScanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
            }
        }

        val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

//        ScanFilter.Builder()
//            .build()
        scanner.startScan(callback)

        scanner.stopScan(callback)
    }

    private val _numThread = MutableLiveData<Int>()
    val numThreads get() = _numThread

    private val _trainingState =
        MutableLiveData(TrainingState.PREPARE)
    val trainingState get() = _trainingState

    private val _captureMode = MutableLiveData(true)
    val captureMode get() = _captureMode

    private val _numberOfSamples = MutableLiveData(TreeMap<String, Int>())
    val numberOfSamples get() = _numberOfSamples

    fun configModel(numThreads: Int) {
        _numThread.value = numThreads
    }

    fun getNumThreads() = numThreads.value

    fun setTrainingState(state: TrainingState) {
        _trainingState.value = state
    }

    fun getTrainingState() = trainingState.value

    fun setCaptureMode(isCapture: Boolean) {
        _captureMode.value = isCapture
    }

    fun getCaptureMode() = captureMode.value

    fun increaseNumberOfSample(className: String) {
        val map: TreeMap<String, Int> = _numberOfSamples.value!!
        val currentNumber: Int = if (map.containsKey(className)) {
            map[className]!!
        } else {
            0
        }
        map[className] = currentNumber + 1
        _numberOfSamples.postValue(map)
    }

    fun getNumberOfSample() = numberOfSamples.value

    enum class TrainingState {
        PREPARE, TRAINING, PAUSE
    }
}