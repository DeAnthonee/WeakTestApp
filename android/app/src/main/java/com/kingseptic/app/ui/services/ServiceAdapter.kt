package com.kingseptic.app.ui.services

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kingseptic.app.databinding.ItemServiceBinding
import com.kingseptic.domain.SepticService

class ServiceAdapter(
    private val onRequest: (SepticService) -> Unit
) : ListAdapter<SepticService, ServiceAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: SepticService) {
            binding.serviceName.text = service.name
            binding.serviceSummary.text = service.summary
            binding.serviceDetails.text = service.details
            binding.emergencyBadge.isVisible(service.isEmergencyEligible)
            binding.requestButton.setOnClickListener { onRequest(service) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<SepticService>() {
        override fun areItemsTheSame(oldItem: SepticService, newItem: SepticService) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SepticService, newItem: SepticService) = oldItem == newItem
    }
}

private fun android.view.View.isVisible(visible: Boolean) {
    visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
}
