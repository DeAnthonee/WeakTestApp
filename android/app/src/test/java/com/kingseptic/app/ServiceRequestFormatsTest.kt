package com.kingseptic.app

import com.kingseptic.app.data.toEmailBody
import com.kingseptic.app.data.toEmailSubject
import com.kingseptic.app.data.toFirestoreMap
import com.kingseptic.domain.ServiceRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class ServiceRequestFormatsTest {

    private val request = ServiceRequest(
        customerName = " Jordan Rivera ",
        phone = "(555) 010-0100",
        address = "123 County Road 12",
        serviceId = "pumping",
        email = "jordan@example.com",
        notes = "Lid is under the deck",
        isEmergency = true,
        createdAtMillis = 1_700_000_000_000L
    )

    @Test
    fun `firestore map uses the field names the security rules expect`() {
        val map = request.toFirestoreMap()
        assertEquals(
            setOf(
                "customerName", "phone", "email", "address", "serviceId", "serviceName",
                "preferredDate", "notes", "isEmergency", "source", "status", "createdAt"
            ),
            map.keys
        )
        assertEquals("Jordan Rivera", map["customerName"])
        assertEquals("5550100100", map["phone"])
        assertEquals("Septic Tank Pumping", map["serviceName"])
        assertEquals("new", map["status"])
        assertEquals(Date(1_700_000_000_000L), map["createdAt"])
    }

    @Test
    fun `email fallback includes every filled-in field`() {
        val body = request.toEmailBody("King Septic")
        assertTrue(body.contains("Name: Jordan Rivera"))
        assertTrue(body.contains("Phone: (555) 010-0100"))
        assertTrue(body.contains("Service: Septic Tank Pumping"))
        assertTrue(body.contains("Emergency: YES"))
        assertTrue(body.contains("Lid is under the deck"))
        assertEquals("EMERGENCY - Service request: Septic Tank Pumping", request.toEmailSubject())
    }
}
