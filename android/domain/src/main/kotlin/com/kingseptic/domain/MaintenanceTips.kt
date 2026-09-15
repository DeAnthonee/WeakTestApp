package com.kingseptic.domain

data class MaintenanceTip(val title: String, val body: String)

object MaintenanceTips {

    val tips: List<MaintenanceTip> = listOf(
        MaintenanceTip(
            "Pump on a schedule",
            "Most homes need pumping every 3 to 5 years. Use the calculator to estimate " +
                "your interval and keep a record of every service."
        ),
        MaintenanceTip(
            "Only flush the three P's",
            "Pee, poo and (toilet) paper. Wipes, even \"flushable\" ones, feminine products, " +
                "paper towels and dental floss clog pumps and fill tanks fast."
        ),
        MaintenanceTip(
            "Keep grease out of the drain",
            "Fats, oils and grease harden in the tank and drain field. Wipe pans and " +
                "put grease in the trash."
        ),
        MaintenanceTip(
            "Spread out water use",
            "Run one load of laundry a day instead of five on Saturday, fix leaking " +
                "toilets, and install low-flow fixtures. Less water means a longer-lasting field."
        ),
        MaintenanceTip(
            "Protect the drain field",
            "Never park or drive on it, keep trees and deep-rooted shrubs away, and " +
                "direct roof drains and sump pumps elsewhere."
        ),
        MaintenanceTip(
            "Go easy on the garbage disposal",
            "A disposal adds up to 50% more solids to your tank. Compost or trash food " +
                "scraps instead, or plan to pump more often."
        ),
        MaintenanceTip(
            "Skip the additives",
            "Your tank already has the bacteria it needs. Chemical and enzyme additives " +
                "are unnecessary and some can damage the system."
        ),
        MaintenanceTip(
            "Know where your system is",
            "Keep a sketch of the tank, lids and drain field location. Risers make lids " +
                "easy to find and save you digging fees at every service."
        )
    )

    val warningSigns: List<String> = listOf(
        "Slow drains or gurgling sounds in the plumbing",
        "Sewage odors inside or near the tank and drain field",
        "Soggy ground, standing water or unusually green grass over the drain field",
        "Sewage backing up into tubs, showers or floor drains",
        "A high-water or pump alarm sounding"
    )
}
