package com.example.triggersensor.presentation

import android.content.Context
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import java.io.File
import java.io.Serializable
import java.util.Calendar
import java.util.Timer

class TriggerListener : TriggerEventListener() {
    private lateinit var context: Context
    private lateinit var logFile: File
    private lateinit var viewModel: MainViewModel
    private lateinit var accListener: MainActivity.AccListener
    private lateinit var wakeLock: WakeLock
    private var timerLengthMins: Float = 0F
    private lateinit var triggerTimerTask: MainActivity.TriggerTimerTask


    fun setValues(
        context: Context,
        logFile: File,
        viewModel: MainViewModel,
        accListener: MainActivity.AccListener,
        wakeLock: WakeLock,
        timerLengthMins: Float,
        triggerTimerTask: MainActivity.TriggerTimerTask
    ) {
        this.context = context
        this.logFile = logFile
        this.viewModel = viewModel
        this.accListener = accListener
        this.wakeLock = wakeLock
        this.timerLengthMins = timerLengthMins
        this.triggerTimerTask = triggerTimerTask
    }

    override fun onTrigger(event: TriggerEvent) {
        Log.d("TriggerSensorState", "Triggered")
        logFile.writeText("${Calendar.getInstance().timeInMillis},Triggered\n")
        viewModel.updateText("Triggered")

        // register
        accListener.register()

        // acquire wake lock for given time (deprecated, but it works)
        wakeLock.acquire((timerLengthMins * 60 * 1e3).toLong())

        // Set timer to reset trigger and stop acc
        Timer().schedule(triggerTimerTask, (timerLengthMins * 60 * 1e3).toLong())

        // these functions are the modern (non-deprecated) ones to use
//             this@com.example.triggersensor.presentation.MainActivity.setTurnScreenOn(true)
//            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // onTrigger, vibrate watch for 1s
        var vibrator: Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            var vibratorManager: VibratorManager = context.getSystemService(ComponentActivity.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else {
            vibrator = context.getSystemService(ComponentActivity.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot((1 * 1e3).toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
    }
}