package com.medithings.blueprint.pref

import android.content.Context
import android.content.SharedPreferences
import com.medithings.blueprint.support.boolean
import com.medithings.blueprint.support.int
import com.medithings.blueprint.support.string
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalPrefData @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        private const val SEARCH_HISTORY = "search_history"
        private const val STORED_PIN = "stored_pin"
        private const val AGC_DATA = "agc_data"

        private const val EPOCH_COUNT = "epoch_count"
        private const val TRAIN_COUNT = "train_count"
        private const val VALIDATION_COUNT = "validation_count"
        private const val TEST_COUNT = "test_count"
        private const val BATCH_SIZE = "batch_size"
        private const val PATIENT_COUNT = "patient_count"
        private const val FIRE_LEV = "fire_lev"
        private const val ALARM_TERM = "alarm_term"
        private const val IS_ADMIN = "is_admin"
        private const val MAIN_INFER_COUNT = "main_infer_count"
    }

    private val preference: SharedPreferences =
        context.getSharedPreferences("localPref", Context.MODE_PRIVATE)

    private var _isIntroShown: Boolean by preference.boolean(false)

    fun isIntroShown(): Boolean = _isIntroShown

    fun setIntroShown() { // 다시 보지 않기라서 바로 true로 설정
        _isIntroShown = true
    }

    private var _isStoredMLData: Boolean by preference.boolean(false)

    fun isStoredMLData(): Boolean = _isStoredMLData

    fun setStoredMLData() { // 다시 보지 않기라서 바로 true로 설정
        _isStoredMLData = true
    }

    private var _storedPin: String? by preference.string("", STORED_PIN)
    fun setStoredPin(pin: String) {
        _storedPin = pin
    }

    fun getStoredPin() = _storedPin

    private var _agcData: String? by preference.string("", AGC_DATA)
    fun setAgcData(value: String) {
        _agcData = value
    }

    fun getAgcData() = _agcData

    private var _trainCount by preference.int(3, TRAIN_COUNT)
    fun setTrainCount(value: Int) {
        _trainCount = value
    }

    fun getTrainCount() = _trainCount

    private var _validationCount by preference.int(2, VALIDATION_COUNT)
    fun setValidationCount(value: Int) {
        _validationCount = value
    }

    fun getValidationCount() = _validationCount

    private var _testCount by preference.int(2, TEST_COUNT)
    fun setTestCount(value: Int) {
        _testCount = value
    }

    fun getTestCount() = _testCount

    private var _epochCount by preference.int(100, EPOCH_COUNT)
    fun setEpochCount(value: Int) {
        _epochCount = value
    }

    fun getEpochCount() = _epochCount

    private var _batchSize by preference.int(2, BATCH_SIZE)
    fun setBatchSize(value: Int) {
        _batchSize = value
    }

    fun getBatchSize() = _batchSize

    private var _patientCount by preference.int(3, PATIENT_COUNT)
    fun setPatientCount(value: Int) {
        _patientCount = value
    }

    fun getPatientCount() = _patientCount

    private var _fireLev by preference.int(5, FIRE_LEV)
    fun setFireLev(value: Int) {
        _fireLev = value
    }

    fun getFireLev() = _fireLev

    private var _alarmTerm by preference.int(30, ALARM_TERM)
    fun setAlarmTerm(value: Int) {
        _alarmTerm = value
    }

    fun getAlarmTerm() = _alarmTerm

    private var _mainInferCount by preference.int(2, MAIN_INFER_COUNT)
    fun setMainInferCount(value: Int) {
        _mainInferCount = value
    }

    fun getMainInferCount() = _mainInferCount

    fun isAdmin() = _isAdmin

    private var _isAdmin by preference.boolean(false, IS_ADMIN)
    fun setIsAdmin(value: Boolean) {
        _isAdmin = value
    }

    fun IsAdmin() = _isAdmin
}
