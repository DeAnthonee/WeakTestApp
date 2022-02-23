package com.example.testapp.networking

import com.example.testapp.modal.ClientPayload
import com.example.testapp.modal.UserPayload
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OurDataBase() {
    private val db = FirebaseFirestore.getInstance()

     @OptIn(ExperimentalCoroutinesApi::class)
     suspend fun getClientPayload(): Flow<ClientPayload> = callbackFlow {
            db.collection("Utils")
            .document("TodaysMenu")
                .get().addOnSuccessListener {
                    val something = it.toObject(ClientPayload::class.java)
                    trySend(something!!)
                }
         awaitClose { channel.close() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserPayload(uid:String): Flow<UserPayload> = callbackFlow {
        db.collection("Users")
            .document(uid)
            .get().addOnSuccessListener {
                val something = it.toObject(UserPayload::class.java)
                trySend(something!!)
            }
        awaitClose { channel.close() }
    }
}

