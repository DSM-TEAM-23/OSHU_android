package com.example.oshu_android.feature.storelist

import androidx.lifecycle.ViewModel
import com.example.oshu_android.data.store.StoreCardResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StoreListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        StoreListUiState(),
    )

    val uiState = _uiState.asStateFlow()

    fun updateStores(
        stores: List<StoreCardResponse>,
    ) {
        _uiState.update { state ->
            state.copy(
                stores = stores,
            )
        }
    }

    fun onSearchQueryChanged(
        query: String,
    ) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
            )
        }
    }

    fun onCategorySelected(
        category: StoreListCategory,
    ) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
            )
        }
    }
}