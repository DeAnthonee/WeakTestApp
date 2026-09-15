package com.kingseptic.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.kingseptic.domain.ServiceRequest
import kotlinx.coroutines.tasks.await

interface ServiceRequestRepository {
    /** True when requests can be submitted to the backend. */
    val isOnline: Boolean

    /** Submits the request and returns its backend id. */
    suspend fun submit(request: ServiceRequest): Result<String>

    companion object {
        /** Firestore collection shared with the website (see firestore.rules). */
        const val COLLECTION = "serviceRequests"
    }
}

class FirestoreServiceRequestRepository(
    private val db: FirebaseFirestore
) : ServiceRequestRepository {

    override val isOnline: Boolean = true

    override suspend fun submit(request: ServiceRequest): Result<String> = runCatching {
        val document = db.collection(ServiceRequestRepository.COLLECTION).document()
        document.set(request.toFirestoreMap()).await()
        document.id
    }
}

/** Used when the app was built without Firebase configuration. */
object NoBackendServiceRequestRepository : ServiceRequestRepository {
    override val isOnline: Boolean = false

    override suspend fun submit(request: ServiceRequest): Result<String> =
        Result.failure(IllegalStateException("Firebase is not configured for this build."))
}
