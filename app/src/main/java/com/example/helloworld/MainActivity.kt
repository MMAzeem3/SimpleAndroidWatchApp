package com.example.helloworld

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.helloworld.databinding.ActivityMainBinding


class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val nextButton: Button = findViewById<Button>(R.id.next_button)
        findViewById<Button>(R.id.next_button).setOnClickListener {
            fun onClick(v: View?) {
                println("clicked")
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            }
        }
        findViewById<Button>(R.id.toast_button).setOnClickListener {
            Toast.makeText(applicationContext, "Hello", Toast.LENGTH_SHORT).show()
        }
    }
//    fun showToast(view: View){
//        val myToast = Toast.makeText(applicationContext, "Hello Toast!", Toast.LENGTH_SHORT)
//        myToast.show()
//    }
}