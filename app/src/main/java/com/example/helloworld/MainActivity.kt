package com.example.helloworld

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.example.helloworld.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    fun showToast(view: View){
        val myToast = Toast.makeText(applicationContext, "Hello Toast!", Toast.LENGTH_SHORT)
        myToast.show()
    }
}