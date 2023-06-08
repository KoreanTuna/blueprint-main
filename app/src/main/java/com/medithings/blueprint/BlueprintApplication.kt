package com.medithings.blueprint

import android.app.Application
import android.os.Environment
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.Nullable
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@HiltAndroidApp
open class BlueprintApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(
            object : Timber.DebugTree() {
                private val LOG_TAG: String = "Timber.DebugTree"

                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    try {
                        val path = "Log"
                        val fileNameTimeStamp: String = SimpleDateFormat(
                            "dd-MM-yyyy",
                            Locale.getDefault()
                        ).format(Date())
                        val logTimeStamp: String = SimpleDateFormat(
                            "E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
                            Locale.getDefault()
                        ).format(Date())
                        val fileName = "$fileNameTimeStamp.html"

                        // Create file
                        val file = generateFile(path, fileName)

                        // If file created or exists save logs
                        if (file != null) {
                            val writer = FileWriter(file, true)
                            writer.append(
                                "<p style=\"background:lightgray;\"><strong "
                                        + "style=\"background:lightblue;\">&nbsp&nbsp"
                            )
                                .append(logTimeStamp)
                                .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
                                .append(tag)
                                .append("</strong> - ")
                                .append(message)
                                .append("</p>")
                            writer.flush()
                            writer.close()
                        }
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "Error while logging into file : $e")
                    }
                }

                override fun createStackElementTag(element: StackTraceElement): String? {
                    // Add log statements line number to the log
                    return super.createStackElementTag(element) + " - " + element.lineNumber
                }

                /*  Helper method to create file*/
                @Nullable
                private fun generateFile(@NonNull path: String, @NonNull fileName: String): File? {
                    var file: File? = null
                    if (isExternalStorageAvailable()) {
                        val root = getExternalFilesDir(null)
                        var dirExists = true
                        if (root != null) {
                            if (!root.exists()) {
                                dirExists = root.mkdirs()
                            }
                        }
                        if (dirExists) {
                            file = File(root, fileName)
                        }
                    }
                    return file
                }

                /* Helper method to determine if external storage is available*/
                private fun isExternalStorageAvailable(): Boolean {
                    return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                }
            }
        )
    }
}