package com.kingseptic.app.ui.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingseptic.app.databinding.ActivityServicesBinding
import com.kingseptic.app.ui.request.RequestServiceActivity
import com.kingseptic.domain.ServiceCatalog

class ServicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val adapter = ServiceAdapter { service ->
            startActivity(RequestServiceActivity.intent(this, serviceId = service.id))
        }
        binding.serviceList.layoutManager = LinearLayoutManager(this)
        binding.serviceList.adapter = adapter
        adapter.submitList(ServiceCatalog.services)
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, ServicesActivity::class.java)
    }
}
