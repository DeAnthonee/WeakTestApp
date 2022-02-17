package com.example.testapp.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.testapp.BasicViewModel
import com.example.testapp.R
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    val viewModel: BasicViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val something = viewModel.sayHello()
        val button: Button = findViewById(R.id.my_button_id)
        button.setOnClickListener {
            SecondActivity.start(this)
        }

        makeMyToast(something)
    }

    private fun makeMyToast(something: String) {
         Toast.makeText(this,something,Toast.LENGTH_SHORT).show()
    }
}