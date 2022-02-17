package com.example.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    val viewModel:BasicViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val something = viewModel.sayHello()

        makeMyToast(something)
    }

    private fun makeMyToast(something: String) {
         Toast.makeText(this,something,Toast.LENGTH_SHORT).show()
    }
}