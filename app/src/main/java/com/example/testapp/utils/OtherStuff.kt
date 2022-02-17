package com.example.testapp

import androidx.lifecycle.ViewModel

interface HelloRepository {
    fun giveHello(): String
}

class HelloRepositoryImpl() : HelloRepository {
    override fun giveHello() = "Hello Koin"
}

class BasicViewModel(val repo: HelloRepository):ViewModel(){

    fun sayHello() = "${repo.giveHello()} from DeAnthonee"

}