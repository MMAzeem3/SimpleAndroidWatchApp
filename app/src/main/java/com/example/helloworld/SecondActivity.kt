package com.example.helloworld

import android.app.Activity
import android.os.Bundle
import com.example.helloworld.databinding.ActivityMainBinding

class SecondActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_page)
    }
}