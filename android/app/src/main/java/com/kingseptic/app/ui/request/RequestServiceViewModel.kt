package com.kingseptic.app.ui.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingseptic.app.data.ServiceRequestRepository
import com.kingseptic.domain.ServiceRequest
import com.kingseptic.domain.ServiceRequestValidator
import kotlinx.coroutines.launch

class RequestServiceViewModel(
    private val repository: ServiceRequestRepository
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Submitting : UiState()
        data class Invalid(val validation: ServiceRequestValidator.Result) : UiState()
        data class Submitted(val id: String) : UiState()
        /** No backend configured: the activity should hand the request to an email app. */
        data class EmailFallback(val request: ServiceRequest) : UiState()
        data class Failed(val message: String) : UiState()
    }

    private val _state = MutableLiveData<UiState>(UiState.Idle)
    val state: LiveData<UiState> = _state

    fun submit(request: ServiceRequest) {
        val validation = ServiceRequestValidator.validate(request)
        if (!validation.isValid) {
            _state.value = UiState.Invalid(validation)
            return
        }
        if (!repository.isOnline) {
            _state.value = UiState.EmailFallback(request)
            return
        }
        _state.value = UiState.Submitting
        viewModelScope.launch {
            _state.value = repository.submit(request).fold(
                onSuccess = { UiState.Submitted(it) },
                onFailure = { UiState.Failed(it.message ?: "Something went wrong. Please call us.") }
            )
        }
    }

    fun reset() {
        _state.value = UiState.Idle
    }
}
