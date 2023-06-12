package com.example.triggersensor.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.triggersensor.presentation.ui.theme.TriggerSensorTheme

class SensorOn : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TriggerSensorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                }
            }
        }
    }
}

class SensorOnViewModel : ViewModel() {
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
fun WearAppSensorOn(text: String) {
    com.example.triggersensor.presentation.theme.TriggerSensorTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.wear.compose.material.MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            androidx.wear.compose.material.Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = androidx.wear.compose.material.MaterialTheme.colors.primary,
                text = "Trigger Sensor\nnSamples: $text"
            )
        }
    }
}