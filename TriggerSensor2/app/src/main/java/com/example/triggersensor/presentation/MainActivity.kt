package com.example.triggersensor.presentation

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.triggersensor.presentation.theme.TriggerSensorTheme
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class MainActivity : ComponentActivity(){
    private lateinit var sensorManager: SensorManager
    private lateinit var triggerSensor: Sensor
    private lateinit var listener: TriggerListener
    private lateinit var accSensor: Sensor
    private var accListener: AccListener = AccListener()
    private var viewModel = MainViewModel()
    private lateinit var logFileStream: FileOutputStream
    private lateinit var accFileStream: FileOutputStream
    private val timerLengthMins: Float = .5F
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp("${viewModel.nSamples}\n${viewModel.text}")
        }

        val dir = SimpleDateFormat("yyyy-MM-dd_HH_mm_ss", Locale.ENGLISH).format(Date())
        File(this.filesDir, dir).mkdir()

        logFileStream = FileOutputStream(File(this.filesDir, "$dir/Log.txt"))
        accFileStream = FileOutputStream(File(this.filesDir, "$dir/data.txt"))
        accFileStream.write("timestamp,x,y,z,real_time_ms\n".toByteArray())
        logFileStream.write("real_time_ms,event\n".toByteArray())
        logFileStream.write("${Calendar.getInstance().timeInMillis},OnCreate()\n".toByteArray())
        Log.d("TriggerSensorState", "onCreate")

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accListener.register()

        listener = TriggerListener()
        triggerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)
        sensorManager.requestTriggerSensor(listener, triggerSensor)

        accListener.unregister()

        // Set up wake lock (deprecated, but it works)
        val powerManager: PowerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "TriggerSensor:keep_screen_on")
    }

    inner class TriggerListener : TriggerEventListener() {
        override fun onTrigger(event: TriggerEvent) {
            Log.d("TriggerSensorState", "Triggered")
            logFileStream.write("${Calendar.getInstance().timeInMillis},Triggered\n".toByteArray())
            viewModel.updateText("Triggered")

            // register
            accListener.register()

            // acquire wake lock for given time
//            wakeLock.acquire((timerLengthMins * 60 * 1e3).toLong())
            // Set timer to reset trigger and stop acc
            Timer().schedule(TriggerTimerTask(), (timerLengthMins * 60 * 1e3).toLong())

            // these functions are the modern (non-deprecated) ones to use
//             this@com.example.triggersensor.presentation.MainActivity.setTurnScreenOn(true)
//            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            // onTrigger, vibrate watch for 1s
            var vibrator: Vibrator

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                var vibratorManager: VibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibrator = vibratorManager.defaultVibrator
            } else {
                vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            }
            vibrator.vibrate(VibrationEffect.createOneShot((1 * 1e3).toLong(), VibrationEffect.DEFAULT_AMPLITUDE))

            // launch SensorOn activity
            startActivity(Intent(this@MainActivity, SensorOn::class.java))
        }
    }

    inner class AccListener : SensorEventListener {
        fun register() {
            logFileStream.write("${Calendar.getInstance().timeInMillis},Acc start\n".toByteArray())
            Log.d("TriggerSensorState", "Acc start")
            accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val samplingPeriodMicroseconds = 1000000/100
            sensorManager.registerListener(this@AccListener, accSensor, samplingPeriodMicroseconds)
        }
        fun unregister() {
            sensorManager.unregisterListener(this@AccListener)
            Log.d("TriggerSensorState", "Acc stop")
            logFileStream.write("${Calendar.getInstance().timeInMillis},Acc stop\n".toByteArray())
        }

        override fun onSensorChanged(event: SensorEvent) {
            Log.d("TriggerSensorOnChange", "${event.values[0]}")
            viewModel.updateNSamples(viewModel.nSamples + 1)
            accFileStream.write("${event.timestamp},${event.values[0]},${event.values[1]},${event.values[2]},${Calendar.getInstance().timeInMillis}\n".toByteArray())
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    inner class TriggerTimerTask : TimerTask() {
        override fun run() {
            // unregister accelerometer, reset trigger, and let go of screen lock
            Log.d("TriggerSensorState", "Timer End")
            logFileStream.write("${Calendar.getInstance().timeInMillis},Timer End\n".toByteArray())
            viewModel.updateText("Timer End")

            accListener.unregister()
            sensorManager.requestTriggerSensor(listener, triggerSensor)

            // this is handled by wakelock for now
//            runOnUiThread {
//                Log.d("TriggerSensorState", "Screen flag clear")
//                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            }

            // on timer end, vibrate watch for 0.5s
            var vibrator: Vibrator

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                var vibratorManager: VibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibrator = vibratorManager.defaultVibrator
            } else {
                vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            }
            vibrator.vibrate(VibrationEffect.createOneShot((0.5 * 1e3).toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    override fun onStart() {
        super.onStart()
        logFileStream.write("${Calendar.getInstance().timeInMillis},OnStart()\n".toByteArray())
        Log.d("TriggerSensorState", "onStart")
    }
    override fun onResume() {
        super.onResume()
        Log.d("TriggerSensorState", "onResume")
        logFileStream.write("${Calendar.getInstance().timeInMillis},OnResume()\n".toByteArray())
    }
    override fun onPause() {
        super.onPause()
        logFileStream.write("${Calendar.getInstance().timeInMillis},onPause()\n".toByteArray())
        Log.d("TriggerSensorState", "onPause")
    }
    override fun onStop() {
        super.onStop()
        logFileStream.write("${Calendar.getInstance().timeInMillis},OnStop()\n".toByteArray())
        Log.d("TriggerSensorState", "onStop")
        viewModel.updateX(0F)
    }
    override fun onDestroy() {
        super.onDestroy()
        logFileStream.write("${Calendar.getInstance().timeInMillis},onDestroy()\n".toByteArray())
        Log.d("TriggerSensorState", "onDestroy")
        accFileStream.close()
        logFileStream.close()
    }
    override fun onRestart() {
        super.onRestart()
        logFileStream.write("${Calendar.getInstance().timeInMillis},onRestart()\n".toByteArray())
        Log.d("TriggerSensorState", "onRestart")
    }
}

class MainViewModel : ViewModel() {
    var x by mutableStateOf(0F)
    var nSamples by mutableStateOf(0)
    var text by mutableStateOf("Created")

    fun updateX(xValue: Float) {
        x = xValue
    }
    fun updateNSamples(_nSamples: Int) {
        nSamples = _nSamples
    }
    fun updateText(_text: String) {
        text = _text
    }
}

@Composable
fun WearApp(text: String) {
    TriggerSensorTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Greeting(text)
        }
    }
}

@Composable
fun Greeting(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = "Trigger Sensor\nnSamples: $text"
    )
}