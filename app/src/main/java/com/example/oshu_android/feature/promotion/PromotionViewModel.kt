package com.example.oshu_android.feature.promotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.oshu_android.data.store.PromotionListResult
import com.example.oshu_android.data.store.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            when (val result = storeRepository.getPromotions()) {
                is PromotionListResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            promotions = result.promotions.map {
                                it.toPromotionItem()
                            },
                        )
                    }
                }

                is PromotionListResult.Failure -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = result.message,
                        )
                    }
                }
            }
        }
    }

    class Factory(
        private val storeRepository: StoreRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            if (modelClass.isAssignableFrom(PromotionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PromotionViewModel(
                    storeRepository = storeRepository,
                ) as T
            }

            throw IllegalArgumentException(
                "Unknown ViewModel class",
            )
        }
    }
}