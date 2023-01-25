package com.example.compose_testing.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var sensorData by mutableStateOf("0")
    fun updateSensorData(value: String){
        sensorData = value
    }
}