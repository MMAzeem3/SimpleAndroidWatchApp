/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.compose_testing.presentation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.compose_testing.R
import com.example.compose_testing.presentation.theme.ComposeTestingTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    val mViewModel: MainViewModel = MainViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val samplingRateHertz = 100
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val samplingPeriodMicroseconds = 1000000/samplingRateHertz
        sensorManager.registerListener(this, sensor, samplingPeriodMicroseconds)

        setContent {
            WearApp("Android")
        }
    }
    override fun onSensorChanged(p0: SensorEvent) {
        mViewModel.updateSensorData(p0.values[0].toString())
//        mViewModel.updateSensorData("10")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    @Composable
    fun WearApp(greetingName: String) {
//    var sensor by remember { mutableStateOf("Default") }
        ComposeTestingTheme {
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
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = mViewModel.sensorData
                )
            }
        }
    }
}
