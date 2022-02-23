package com.example.testapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.networking.OurDataBase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

interface HelloRepository {
    fun giveHello(): String
}

class HelloRepositoryImpl() : HelloRepository {
    override fun giveHello() = "Hello Koin"
}

class BasicViewModel(val repo: HelloRepository):ViewModel(){

    fun sayHello() = "${repo.giveHello()} from DeAnthonee"

    init {
        val ourDataBase = OurDataBase()
        viewModelScope.launch {
            ourDataBase.getClientPayload().collect {
                Log.d("mixo1","$it")
            }
        }


    }

    fun getClientPayloadPlease(){}

}