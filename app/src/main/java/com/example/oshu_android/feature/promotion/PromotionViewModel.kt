package com.example.oshu_android.feature.promotion

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PromotionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        PromotionUiState(),
    )

    val uiState = _uiState.asStateFlow()

    fun onCategorySelected(
        category: PromotionCategory,
    ) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
            )
        }
    }
}