package com.example.testapp.activites

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testapp.R

class SecondActivity : AppCompatActivity() {

    companion object {

        fun start(context: Context){
            val intent = Intent(context,SecondActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }
}