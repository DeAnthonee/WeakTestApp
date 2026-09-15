package com.kingseptic.domain

/**
 * A customer's request for service or a quote. This is the shape stored in the
 * `serviceRequests` Firestore collection by both the app and the website.
 */
data class ServiceRequest(
    val customerName: String,
    val phone: String,
    val address: String,
    val serviceId: String,
    val email: String = "",
    val preferredDate: String = "",
    val notes: String = "",
    val isEmergency: Boolean = false,
    val source: String = SOURCE_ANDROID,
    val status: String = STATUS_NEW,
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    val service: SepticService? get() = ServiceCatalog.findById(serviceId)

    companion object {
        const val SOURCE_ANDROID = "android"
        const val SOURCE_WEB = "web"

        const val STATUS_NEW = "new"
        const val STATUS_SCHEDULED = "scheduled"
        const val STATUS_DONE = "done"
        const val STATUS_CANCELLED = "cancelled"
    }
}
