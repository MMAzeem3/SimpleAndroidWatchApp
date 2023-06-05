/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.triggersensor.presentation

import android.annotation.SuppressLint
import android.hardware.*
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.triggersensor.presentation.theme.TriggerSensorTheme
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : ComponentActivity(){
    private lateinit var sensorManager: SensorManager
    private lateinit var triggerSensor: Sensor
    private lateinit var listener: TriggerListener
    private lateinit var accSensor: Sensor
    private var accListener: AccListener = AccListener()
    private var viewModel = MainViewModel()
    private lateinit var logFileStream: FileOutputStream
    private lateinit var accFileStream: FileOutputStream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp(viewModel.x)
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        listener = TriggerListener()
        triggerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)
        sensorManager.requestTriggerSensor(listener, triggerSensor)

        logFileStream = FileOutputStream(File(this.filesDir, "Log.txt"))
        accFileStream = FileOutputStream(File(this.filesDir, "data.txt"))
        accFileStream.write("timestamp,x,y,z,real_time_ms\n".toByteArray())
        logFileStream.write("real_time_ms,event\n".toByteArray())
        logFileStream.write("${Calendar.getInstance().timeInMillis},OnCreate()".toByteArray())
    }

    override fun onStart() {
        super.onStart()
        logFileStream.write("${Calendar.getInstance().timeInMillis},OnStart()".toByteArray())
    }
    override fun onResume() {
        super.onResume()
        logFileStream.write("${Calendar.getInstance().timeInMillis},OnResume()".toByteArray())
    }
    override fun onPause() {
        super.onPause()
        logFileStream.write("${Calendar.getInstance().timeInMillis},onPause()".toByteArray())
    }
    override fun onStop() {
        super.onStop()
        logFileStream.write("${Calendar.getInstance().timeInMillis},OnStop()".toByteArray())
        unregisterAcc()
        sensorManager.requestTriggerSensor(listener, triggerSensor)
        viewModel.updateX(0F)
    }
    override fun onDestroy() {
        super.onDestroy()
        logFileStream.write("${Calendar.getInstance().timeInMillis},onDestroy()".toByteArray())
        accFileStream.close()
        logFileStream.close()
    }
    override fun onRestart() {
        super.onRestart()
        logFileStream.write("${Calendar.getInstance().timeInMillis},onRestart()".toByteArray())
    }

    fun unregisterAcc() {
        sensorManager.unregisterListener(accListener)
        logFileStream.write("${Calendar.getInstance().timeInMillis},Acc stop".toByteArray())
    }

    inner class TriggerListener : TriggerEventListener() {
        override fun onTrigger(event: TriggerEvent) {
            Log.d("TriggerSensorOnTrigger", "${event.timestamp}")
            logFileStream.write("${Calendar.getInstance().timeInMillis},Triggered".toByteArray())
            // Re-register trigger sensor after trigger

            // Register acc sensor
            // unregister if registered

            // register
            accListener.register()
        }
    }

    inner class AccListener : SensorEventListener {
        fun register() {
            logFileStream.write("${Calendar.getInstance().timeInMillis},Acc start\n".toByteArray())
            accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val samplingPeriodMicroseconds = 1000000/100
            sensorManager.registerListener(this@AccListener, accSensor, samplingPeriodMicroseconds)
        }

        override fun onSensorChanged(event: SensorEvent) {
            Log.d("TriggerSensorOnChange", "${event.values[0]}")
            viewModel.updateX(event.values[0])
            accFileStream.write("${event.timestamp},${event.values[0]},${event.values[1]},${event.values[2]},${Calendar.getInstance().timeInMillis}\n".toByteArray())
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }
}

class MainViewModel : ViewModel() {
    var x by mutableStateOf(0F)

    fun updateX(xValue: Float) {
        x = xValue
    }
}

@Composable
fun WearApp(x: Float) {
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
            Greeting(x = "$x")
        }
    }
}

@Composable
fun Greeting(x: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = "Trigger Sensor\nx: $x"
    )
}