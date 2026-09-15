package com.kingseptic.app.ui.request

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.kingseptic.app.BusinessInfo
import com.kingseptic.app.R
import com.kingseptic.app.data.toEmailBody
import com.kingseptic.app.data.toEmailSubject
import com.kingseptic.app.databinding.ActivityRequestServiceBinding
import com.kingseptic.domain.ServiceCatalog
import com.kingseptic.domain.ServiceRequest
import com.kingseptic.domain.ServiceRequestValidator.Field
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RequestServiceActivity : AppCompatActivity() {

    private val business: BusinessInfo by inject()
    private val viewModel: RequestServiceViewModel by viewModel()
    private lateinit var binding: ActivityRequestServiceBinding

    private val services = ServiceCatalog.services
    private var selectedServiceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.callInsteadButton.text = getString(R.string.action_call_number, business.phoneDisplay)
        binding.callInsteadButton.setOnClickListener { startActivity(business.dialIntent()) }

        setUpServicePicker()
        intent.getStringExtra(EXTRA_SERVICE_ID)?.let { selectService(it) }
        binding.emergencySwitch.isChecked = intent.getBooleanExtra(EXTRA_EMERGENCY, false)

        binding.submitButton.setOnClickListener { submit() }
        binding.doneButton.setOnClickListener { finish() }

        viewModel.state.observe(this, ::render)
    }

    private fun setUpServicePicker() {
        val names = services.map { it.name }
        binding.serviceInput.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, names))
        binding.serviceInput.setOnItemClickListener { _, _, position, _ ->
            selectedServiceId = services[position].id
            binding.serviceLayout.error = null
        }
    }

    private fun selectService(id: String) {
        val service = ServiceCatalog.findById(id) ?: return
        selectedServiceId = service.id
        binding.serviceInput.setText(service.name, false)
        if (service.id == "emergency") binding.emergencySwitch.isChecked = true
    }

    private fun collectRequest(): ServiceRequest = ServiceRequest(
        customerName = binding.nameInput.text?.toString().orEmpty(),
        phone = binding.phoneInput.text?.toString().orEmpty(),
        email = binding.emailInput.text?.toString().orEmpty(),
        address = binding.addressInput.text?.toString().orEmpty(),
        serviceId = selectedServiceId.orEmpty(),
        preferredDate = binding.dateInput.text?.toString().orEmpty(),
        notes = binding.notesInput.text?.toString().orEmpty(),
        isEmergency = binding.emergencySwitch.isChecked,
        source = ServiceRequest.SOURCE_ANDROID
    )

    private fun submit() {
        clearErrors()
        viewModel.submit(collectRequest())
    }

    private fun clearErrors() {
        binding.nameLayout.error = null
        binding.phoneLayout.error = null
        binding.emailLayout.error = null
        binding.addressLayout.error = null
        binding.serviceLayout.error = null
    }

    private fun render(state: RequestServiceViewModel.UiState) {
        val busy = state is RequestServiceViewModel.UiState.Submitting
        binding.progress.visibility = if (busy) View.VISIBLE else View.GONE
        binding.submitButton.isEnabled = !busy

        when (state) {
            RequestServiceViewModel.UiState.Idle,
            RequestServiceViewModel.UiState.Submitting -> Unit

            is RequestServiceViewModel.UiState.Invalid -> {
                val errors = state.validation
                binding.nameLayout.error = errors.errorFor(Field.NAME)
                binding.phoneLayout.error = errors.errorFor(Field.PHONE)
                binding.emailLayout.error = errors.errorFor(Field.EMAIL)
                binding.addressLayout.error = errors.errorFor(Field.ADDRESS)
                binding.serviceLayout.error = errors.errorFor(Field.SERVICE)
                viewModel.reset()
            }

            is RequestServiceViewModel.UiState.Submitted -> showSuccess(
                getString(R.string.request_success_body, business.phoneDisplay)
            )

            is RequestServiceViewModel.UiState.EmailFallback -> {
                sendByEmail(state.request)
                viewModel.reset()
            }

            is RequestServiceViewModel.UiState.Failed -> {
                Snackbar.make(binding.root, getString(R.string.request_failed, state.message), Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_call) { startActivity(business.dialIntent()) }
                    .show()
                viewModel.reset()
            }
        }
    }

    private fun sendByEmail(request: ServiceRequest) {
        val intent = business.emailIntent(request.toEmailSubject(), request.toEmailBody(business.name))
        try {
            startActivity(intent)
            showSuccess(getString(R.string.request_email_body, business.phoneDisplay))
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.root, getString(R.string.request_no_email_app, business.phoneDisplay), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_call) { startActivity(business.dialIntent()) }
                .show()
        }
    }

    private fun showSuccess(message: String) {
        binding.formGroup.visibility = View.GONE
        binding.successGroup.visibility = View.VISIBLE
        binding.successBody.text = message
    }

    companion object {
        private const val EXTRA_SERVICE_ID = "service_id"
        private const val EXTRA_EMERGENCY = "emergency"

        fun intent(context: Context, serviceId: String? = null, emergency: Boolean = false): Intent =
            Intent(context, RequestServiceActivity::class.java)
                .putExtra(EXTRA_SERVICE_ID, serviceId)
                .putExtra(EXTRA_EMERGENCY, emergency)
    }
}
