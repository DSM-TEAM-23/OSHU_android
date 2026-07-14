package com.example.oshu_android.feature.storedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oshu_android.data.network.ApiResult
import com.example.oshu_android.data.store.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoreDetailViewModel(
    private val storeRepository: StoreRepository,
    private val storeId: Long,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StoreDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
            )

            when (val result = storeRepository.getStoreDetail(storeId)) {
                is ApiResult.Success -> {
                    _uiState.value = StoreDetailUiState(
                        store = result.data,
                    )
                }

                is ApiResult.Failure -> {
                    _uiState.value = StoreDetailUiState(
                        errorMessage = result.message,
                    )
                }
            }
        }
    }

    class Factory(
        private val storeRepository: StoreRepository,
        private val storeId: Long,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoreDetailViewModel::class.java)) {
                return StoreDetailViewModel(
                    storeRepository = storeRepository,
                    storeId = storeId,
                ) as T
            }
            throw IllegalArgumentException("알 수 없는 ViewModel입니다.")
        }
    }
}
