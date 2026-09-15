package com.kingseptic.app.di

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.kingseptic.app.BusinessInfo
import com.kingseptic.app.data.FirestoreServiceRequestRepository
import com.kingseptic.app.data.NoBackendServiceRequestRepository
import com.kingseptic.app.data.ServiceRequestRepository
import com.kingseptic.app.ui.request.RequestServiceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { BusinessInfo(androidContext()) }

    // Firestore is used when the app was built with a google-services.json;
    // otherwise requests are handed off to the phone's email app.
    single<ServiceRequestRepository> {
        if (FirebaseApp.getApps(androidContext()).isEmpty()) {
            NoBackendServiceRequestRepository
        } else {
            FirestoreServiceRequestRepository(FirebaseFirestore.getInstance())
        }
    }

    viewModel { RequestServiceViewModel(get()) }
}
