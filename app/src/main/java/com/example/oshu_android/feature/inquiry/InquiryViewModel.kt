package com.example.oshu_android.feature.inquiry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oshu_android.data.inquiry.InquiryRepository
import com.example.oshu_android.data.inquiry.InquiryRequest
import com.example.oshu_android.data.network.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InquiryViewModel(
    private val inquiryRepository: InquiryRepository,
    private val storeId: Long,
) : ViewModel() {
    private val _uiState = MutableStateFlow(InquiryUiState())
    val uiState = _uiState.asStateFlow()

    fun onTitleChanged(value: String) { _uiState.value = _uiState.value.copy(title = value) }
    fun onNameChanged(value: String) { _uiState.value = _uiState.value.copy(name = value) }
    fun onNumberChanged(value: String) { _uiState.value = _uiState.value.copy(number = value) }
    fun onContentChanged(value: String) { _uiState.value = _uiState.value.copy(content = value) }

    fun submit() {
        val state = _uiState.value
        if (state.title.isBlank() || state.name.isBlank() || state.number.isBlank() || state.content.isBlank()) {
            _uiState.value = state.copy(errorMessage = "모든 항목을 입력해주세요.")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true, errorMessage = null)
            when (val result = inquiryRepository.createInquiry(
                storeId = storeId,
                request = InquiryRequest(
                    title = state.title,
                    content = state.content,
                    name = state.name,
                    number = state.number,
                ),
            )) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(isSubmitting = false, submitted = true)
                is ApiResult.Failure -> _uiState.value = _uiState.value.copy(isSubmitting = false, errorMessage = result.message)
            }
        }
    }

    class Factory(
        private val inquiryRepository: InquiryRepository,
        private val storeId: Long,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InquiryViewModel::class.java)) {
                return InquiryViewModel(inquiryRepository, storeId) as T
            }
            throw IllegalArgumentException("알 수 없는 ViewModel입니다.")
        }
    }
}
