package com.kingseptic.app.data

import com.kingseptic.domain.ServiceRequest
import com.kingseptic.domain.ServiceRequestValidator
import java.util.Date

/** Shape of a `serviceRequests` document. Field names must match firestore.rules. */
fun ServiceRequest.toFirestoreMap(): Map<String, Any> = mapOf(
    "customerName" to customerName.trim(),
    "phone" to (ServiceRequestValidator.normalizePhone(phone) ?: phone.trim()),
    "email" to email.trim(),
    "address" to address.trim(),
    "serviceId" to serviceId,
    "serviceName" to (service?.name ?: serviceId),
    "preferredDate" to preferredDate.trim(),
    "notes" to notes.trim(),
    "isEmergency" to isEmergency,
    "source" to source,
    "status" to status,
    "createdAt" to Date(createdAtMillis)
)

/** Plain-text version of a request, used for the email fallback. */
fun ServiceRequest.toEmailBody(businessName: String): String = buildString {
    appendLine("New service request from the $businessName app")
    appendLine()
    appendLine("Name: ${customerName.trim()}")
    appendLine("Phone: ${ServiceRequestValidator.formatPhone(phone)}")
    if (email.isNotBlank()) appendLine("Email: ${email.trim()}")
    appendLine("Address: ${address.trim()}")
    appendLine("Service: ${service?.name ?: serviceId}")
    if (preferredDate.isNotBlank()) appendLine("Preferred date: ${preferredDate.trim()}")
    appendLine("Emergency: ${if (isEmergency) "YES" else "No"}")
    if (notes.isNotBlank()) {
        appendLine()
        appendLine("Notes:")
        appendLine(notes.trim())
    }
}

fun ServiceRequest.toEmailSubject(): String =
    (if (isEmergency) "EMERGENCY - " else "") + "Service request: ${service?.name ?: serviceId}"
