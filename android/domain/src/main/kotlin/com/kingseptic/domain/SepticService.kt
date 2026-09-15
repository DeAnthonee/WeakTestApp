package com.kingseptic.domain

/**
 * A service the business offers. [id] is stable and shared with the website's
 * service catalog (website/js/config.js) and with Firestore documents, so keep
 * the two lists in sync when you add or remove a service.
 */
data class SepticService(
    val id: String,
    val name: String,
    val summary: String,
    val details: String,
    val isEmergencyEligible: Boolean = false
)

object ServiceCatalog {

    val services: List<SepticService> = listOf(
        SepticService(
            id = "pumping",
            name = "Septic Tank Pumping",
            summary = "Routine pumping and cleaning to keep your system healthy.",
            details = "We pump the tank, remove sludge and scum, check the baffles and " +
                "inlet/outlet, and leave your site clean. Most households need this " +
                "every 3 to 5 years.",
            isEmergencyEligible = true
        ),
        SepticService(
            id = "inspection",
            name = "Septic Inspections",
            summary = "Real-estate and routine inspections with a written report.",
            details = "A full inspection of the tank, distribution box and drain field, " +
                "including a written report suitable for home sales and refinancing."
        ),
        SepticService(
            id = "repair",
            name = "Septic Repairs",
            summary = "Baffles, pumps, lids, risers, lines and more.",
            details = "We diagnose and repair failed baffles, effluent pumps, float switches, " +
                "cracked lids, collapsed lines and other common failures.",
            isEmergencyEligible = true
        ),
        SepticService(
            id = "installation",
            name = "New System Installation",
            summary = "Design, permitting and installation of new septic systems.",
            details = "From soil evaluation and permits to the final inspection, we handle " +
                "conventional and alternative system installs for new builds and replacements."
        ),
        SepticService(
            id = "drain-field",
            name = "Drain Field Services",
            summary = "Drain field evaluation, restoration and replacement.",
            details = "Standing water, soggy ground or sewage odors usually point to the drain " +
                "field. We evaluate the field and recommend restoration or replacement."
        ),
        SepticService(
            id = "grease-trap",
            name = "Grease Trap Cleaning",
            summary = "Scheduled grease trap service for restaurants and kitchens.",
            details = "Recurring grease trap pumping and cleaning for commercial kitchens, " +
                "with service records for your health inspector."
        ),
        SepticService(
            id = "emergency",
            name = "24/7 Emergency Service",
            summary = "Backups, overflows and alarms, any time of day.",
            details = "Sewage backing up, an alarm sounding or a tank overflowing is an " +
                "emergency. Call us and we will dispatch a truck as soon as possible.",
            isEmergencyEligible = true
        )
    )

    fun findById(id: String): SepticService? = services.firstOrNull { it.id == id }

    fun contains(id: String): Boolean = findById(id) != null
}
