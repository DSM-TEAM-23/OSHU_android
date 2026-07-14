package com.example.oshu_android.feature.promotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oshu_android.data.store.PromotionListResult
import com.example.oshu_android.data.store.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PromotionViewModel(
    private val storeRepository: StoreRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PromotionUiState(),
    )

    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun onCategorySelected(
        category: PromotionCategory,
    ) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
        )
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
            )

            when (val result = storeRepository.getPromotions()) {
                is PromotionListResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        promotions = result.promotions.map { promotion ->
                            promotion.toPromotionItem()
                        },
                        isLoading = false,
                        errorMessage = null,
                    )
                }

                is PromotionListResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                    )
                }
            }
        }
    }

    class Factory(
        private val storeRepository: StoreRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
        ): T {
            if (modelClass.isAssignableFrom(PromotionViewModel::class.java)) {
                return PromotionViewModel(
                    storeRepository = storeRepository,
                ) as T
            }

            throw IllegalArgumentException(
                "알 수 없는 ViewModel입니다.",
            )
        }
    }
}