package com.medithings.blueprint.support

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

val String.Companion.EMPTY
    get() = ""

fun Context.isServiceRunning(serviceClassName: String): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val services = activityManager.getRunningServices(Integer.MAX_VALUE)
    return services.find { it.service.className == serviceClassName } != null
}

private val exceptionHandler = CoroutineExceptionHandler { _, t ->
    Log.e("COROUTINE-EXCEPTION", "Uncaught exception", t)
}

fun CoroutineScope.launchWithCatch(block: suspend CoroutineScope.() -> Unit) =
    launch(Job() + exceptionHandler) {
        block()
    }
