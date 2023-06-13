package com.example.triggersensor.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.triggersensor.presentation.ui.theme.TriggerSensor2Theme
import java.util.Calendar

class SensorOn : ComponentActivity() {
    val viewModel = MainViewModel2()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp2(text = "nSamples: ${viewModel.nSamples}")
        }
    }

    override fun onStart() {
        super.onStart()
//        logFileStream.write("${Calendar.getInstance().timeInMillis},OnStart()\n".toByteArray())
        Log.d("TriggerSensorState", "onStart2")
    }
    override fun onResume() {
        super.onResume()
        Log.d("TriggerSensorState", "onResume2")
//        logFileStream.write("${Calendar.getInstance().timeInMillis},OnResume()\n".toByteArray())
    }
    override fun onPause() {
        super.onPause()
//        logFileStream.write("${Calendar.getInstance().timeInMillis},onPause()\n".toByteArray())
        Log.d("TriggerSensorState", "onPause2")
    }
    override fun onStop() {
        super.onStop()
//        logFileStream.write("${Calendar.getInstance().timeInMillis},OnStop()\n".toByteArray())
        Log.d("TriggerSensorState", "onStop2")
//        viewModel.updateX(0F)
    }
    override fun onDestroy() {
        super.onDestroy()
//        logFileStream.write("${Calendar.getInstance().timeInMillis},onDestroy()\n".toByteArray())
        Log.d("TriggerSensorState", "onDestroy2")
//        accFileStream.close()
//        logFileStream.close()
    }
    override fun onRestart() {
        super.onRestart()
//        logFileStream.write("${Calendar.getInstance().timeInMillis},onRestart()\n".toByteArray())
        Log.d("TriggerSensorState", "onRestart2")
    }
}

class MainViewModel2: ViewModel() {
    var nSamples by mutableStateOf(0F)

    fun updateNSample(_nSamples: Float) {
        nSamples = _nSamples
    }
}

@Composable
fun WearApp2(text: String) {
    TriggerSensor2Theme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Greeting2("Android")
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}