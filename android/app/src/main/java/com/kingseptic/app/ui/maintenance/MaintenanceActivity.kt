package com.kingseptic.app.ui.maintenance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.kingseptic.app.R
import com.kingseptic.app.databinding.ActivityMaintenanceBinding
import com.kingseptic.app.databinding.ItemTipBinding
import com.kingseptic.app.ui.request.RequestServiceActivity
import com.kingseptic.domain.MaintenanceTips
import com.kingseptic.domain.PumpingScheduleCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MaintenanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMaintenanceBinding
    private var lastPumped: LocalDate? = null
    private val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.calculateButton.setOnClickListener { calculate() }
        binding.lastPumpedInput.setOnClickListener { pickLastPumpedDate() }
        binding.schedulePumpingButton.setOnClickListener {
            startActivity(RequestServiceActivity.intent(this, serviceId = "pumping"))
        }

        renderWarningSigns()
        renderTips()
    }

    private fun calculate() {
        binding.tankLayout.error = null
        binding.occupantsLayout.error = null

        val tank = binding.tankInput.text?.toString()?.toIntOrNull()
        val occupants = binding.occupantsInput.text?.toString()?.toIntOrNull()
        if (tank == null || tank !in PumpingScheduleCalculator.MIN_TANK_GALLONS..PumpingScheduleCalculator.MAX_TANK_GALLONS) {
            binding.tankLayout.error = getString(
                R.string.calc_error_tank,
                PumpingScheduleCalculator.MIN_TANK_GALLONS,
                PumpingScheduleCalculator.MAX_TANK_GALLONS
            )
            return
        }
        if (occupants == null || occupants !in PumpingScheduleCalculator.MIN_OCCUPANTS..PumpingScheduleCalculator.MAX_OCCUPANTS) {
            binding.occupantsLayout.error = getString(
                R.string.calc_error_occupants,
                PumpingScheduleCalculator.MIN_OCCUPANTS,
                PumpingScheduleCalculator.MAX_OCCUPANTS
            )
            return
        }

        val disposal = binding.disposalSwitch.isChecked
        val estimate = PumpingScheduleCalculator.estimate(tank, occupants, disposal)

        binding.resultInterval.text = getString(R.string.calc_result_interval, estimate.recommendedIntervalYears)
        binding.resultDetail.text = if (estimate.estimatedYearsUntilFull > estimate.recommendedIntervalYears) {
            getString(R.string.calc_result_capped, estimate.estimatedYearsUntilFull)
        } else {
            getString(R.string.calc_result_note)
        }

        val last = lastPumped
        if (last != null) {
            val due = PumpingScheduleCalculator.nextDueDate(last, tank, occupants, disposal)
            val label = if (due.isBefore(LocalDate.now())) R.string.calc_result_overdue else R.string.calc_result_due
            binding.resultDue.text = getString(label, due.format(dateFormat))
            binding.resultDue.visibility = View.VISIBLE
        } else {
            binding.resultDue.visibility = View.GONE
        }
        binding.resultCard.visibility = View.VISIBLE
    }

    private fun pickLastPumpedDate() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.calc_last_pumped)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        picker.addOnPositiveButtonClickListener { millis ->
            val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
            lastPumped = date
            binding.lastPumpedInput.setText(date.format(dateFormat))
        }
        picker.show(supportFragmentManager, "lastPumped")
    }

    private fun renderWarningSigns() {
        binding.warningSigns.text = MaintenanceTips.warningSigns.joinToString("\n") { "• $it" }
    }

    private fun renderTips() {
        val inflater = LayoutInflater.from(this)
        MaintenanceTips.tips.forEach { tip ->
            val item = ItemTipBinding.inflate(inflater, binding.tipsContainer, false)
            item.tipTitle.text = tip.title
            item.tipBody.text = tip.body
            binding.tipsContainer.addView(item.root)
        }
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, MaintenanceActivity::class.java)
    }
}
