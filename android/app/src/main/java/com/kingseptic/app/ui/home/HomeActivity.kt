package com.kingseptic.app.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kingseptic.app.BusinessInfo
import com.kingseptic.app.R
import com.kingseptic.app.databinding.ActivityHomeBinding
import com.kingseptic.app.ui.maintenance.MaintenanceActivity
import com.kingseptic.app.ui.request.RequestServiceActivity
import com.kingseptic.app.ui.services.ServicesActivity
import org.koin.android.ext.android.inject

class HomeActivity : AppCompatActivity() {

    private val business: BusinessInfo by inject()
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = business.name
        binding.heroTitle.text = business.tagline
        binding.heroSubtitle.text = getString(R.string.home_hero_subtitle, business.serviceArea)
        binding.callButton.text = getString(R.string.action_call_number, business.phoneDisplay)
        binding.hoursText.text = business.hours
        binding.emailText.text = business.email

        binding.callButton.setOnClickListener { startActivity(business.dialIntent()) }
        binding.emergencyCallButton.setOnClickListener { startActivity(business.dialIntent()) }
        binding.requestButton.setOnClickListener {
            startActivity(RequestServiceActivity.intent(this))
        }
        binding.servicesCard.setOnClickListener {
            startActivity(ServicesActivity.intent(this))
        }
        binding.maintenanceCard.setOnClickListener {
            startActivity(MaintenanceActivity.intent(this))
        }
        binding.emailText.setOnClickListener {
            startActivity(business.emailIntent(getString(R.string.email_general_subject), ""))
        }
    }
}
