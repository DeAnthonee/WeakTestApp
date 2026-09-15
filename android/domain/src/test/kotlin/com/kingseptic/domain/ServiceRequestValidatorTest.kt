package com.kingseptic.domain

import com.kingseptic.domain.ServiceRequestValidator.Field
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceRequestValidatorTest {

    private val valid = ServiceRequest(
        customerName = "Jordan Rivera",
        phone = "(555) 010-0100",
        address = "123 County Road 12, Anytown",
        serviceId = "pumping",
        email = "jordan@example.com",
        createdAtMillis = 0L
    )

    @Test
    fun `a complete request is valid`() {
        val result = ServiceRequestValidator.validate(valid)
        assertTrue(result.errors.toString(), result.isValid)
    }

    @Test
    fun `email is optional but must be well formed when given`() {
        assertTrue(ServiceRequestValidator.validate(valid.copy(email = "")).isValid)
        val result = ServiceRequestValidator.validate(valid.copy(email = "not-an-email"))
        assertFalse(result.isValid)
        assertEquals(setOf(Field.EMAIL), result.errors.keys)
    }

    @Test
    fun `missing name address phone and service are each reported`() {
        val result = ServiceRequestValidator.validate(
            ServiceRequest(customerName = "", phone = "12", address = "", serviceId = "nope", createdAtMillis = 0L)
        )
        assertEquals(setOf(Field.NAME, Field.PHONE, Field.ADDRESS, Field.SERVICE), result.errors.keys)
    }

    @Test
    fun `phone numbers are normalized to ten digits`() {
        assertEquals("5550100100", ServiceRequestValidator.normalizePhone("555-010-0100"))
        assertEquals("5550100100", ServiceRequestValidator.normalizePhone("+1 (555) 010-0100"))
        assertNull(ServiceRequestValidator.normalizePhone("555-0100"))
        assertNull(ServiceRequestValidator.normalizePhone("2 555 010 0100"))
    }

    @Test
    fun `phone numbers are formatted for display`() {
        assertEquals("(555) 010-0100", ServiceRequestValidator.formatPhone("5550100100"))
        assertEquals("garbage", ServiceRequestValidator.formatPhone("garbage"))
    }
}
