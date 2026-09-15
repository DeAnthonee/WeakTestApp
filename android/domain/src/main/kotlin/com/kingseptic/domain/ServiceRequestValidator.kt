package com.kingseptic.domain

object ServiceRequestValidator {

    enum class Field { NAME, PHONE, EMAIL, ADDRESS, SERVICE }

    data class Result(val errors: Map<Field, String>) {
        val isValid: Boolean get() = errors.isEmpty()
        fun errorFor(field: Field): String? = errors[field]
    }

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validate(request: ServiceRequest): Result {
        val errors = mutableMapOf<Field, String>()

        if (request.customerName.trim().length < 2) {
            errors[Field.NAME] = "Please enter your name."
        }
        if (normalizePhone(request.phone) == null) {
            errors[Field.PHONE] = "Please enter a valid 10-digit phone number."
        }
        val email = request.email.trim()
        if (email.isNotEmpty() && !EMAIL_REGEX.matches(email)) {
            errors[Field.EMAIL] = "Please enter a valid email address."
        }
        if (request.address.trim().length < 5) {
            errors[Field.ADDRESS] = "Please enter the service address."
        }
        if (!ServiceCatalog.contains(request.serviceId)) {
            errors[Field.SERVICE] = "Please choose a service."
        }
        return Result(errors)
    }

    /**
     * Returns the 10 digits of a North American phone number, or null if the
     * input is not a usable phone number. A leading country code of 1 is accepted.
     */
    fun normalizePhone(raw: String): String? {
        val digits = raw.filter { it.isDigit() }
        return when {
            digits.length == 10 -> digits
            digits.length == 11 && digits.startsWith("1") -> digits.substring(1)
            else -> null
        }
    }

    /** Formats 10 digits as (555) 010-0100; other input is returned unchanged. */
    fun formatPhone(raw: String): String {
        val digits = normalizePhone(raw) ?: return raw
        return "(${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
    }
}
