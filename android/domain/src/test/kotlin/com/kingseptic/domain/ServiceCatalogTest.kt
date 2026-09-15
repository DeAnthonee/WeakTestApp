package com.kingseptic.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceCatalogTest {

    @Test
    fun `service ids are unique and url safe`() {
        val ids = ServiceCatalog.services.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        ids.forEach { assertTrue(it, Regex("^[a-z][a-z0-9-]*$").matches(it)) }
    }

    @Test
    fun `lookup by id`() {
        assertEquals("Septic Tank Pumping", ServiceCatalog.findById("pumping")?.name)
        assertNull(ServiceCatalog.findById("missing"))
    }

    @Test
    fun `a request resolves its service`() {
        val request = ServiceRequest("A B", "5550100100", "1 Main St", "inspection", createdAtMillis = 0L)
        assertEquals("inspection", request.service?.id)
    }
}
