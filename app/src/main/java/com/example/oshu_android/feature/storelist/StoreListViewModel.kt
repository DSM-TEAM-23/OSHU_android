package com.example.oshu_android.feature.storelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oshu_android.data.store.StoreListResult
import com.example.oshu_android.data.store.StoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoreListViewModel(
    private val storeRepository: StoreRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        StoreListUiState(),
    )

    val uiState = _uiState.asStateFlow()

    private var requestJob: Job? = null

    init {
        refresh()
    }

    fun updateStores(
        stores: List<com.example.oshu_android.data.store.StoreCardResponse>,
    ) {
        if (stores.isEmpty()) {
            return
        }

        _uiState.value = _uiState.value.copy(
            stores = stores,
            isLoading = false,
            errorMessage = null,
        )
    }

    fun onSearchQueryChanged(
        query: String,
    ) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
        )

        loadStores(
            delayMillis = 350L,
        )
    }

    fun onCategorySelected(
        category: StoreListCategory,
    ) {
        if (_uiState.value.selectedCategory == category) {
            return
        }

        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
        )

        refresh()
    }

    fun refresh() {
        loadStores(
            delayMillis = 0L,
        )
    }

    private fun loadStores(
        delayMillis: Long,
    ) {
        requestJob?.cancel()

        requestJob = viewModelScope.launch {
            if (delayMillis > 0L) {
                delay(delayMillis)
            }

            val currentState = _uiState.value

            _uiState.value = currentState.copy(
                isLoading = true,
                errorMessage = null,
            )

            when (
                val result = storeRepository.getStores(
                    keyword = currentState.searchQuery
                        .trim()
                        .ifBlank { null },
                    category = currentState.selectedCategory.apiCategory,
                )
            ) {
                is StoreListResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        stores = result.stores,
                        isLoading = false,
                        errorMessage = null,
                    )
                }

                is StoreListResult.Failure -> {
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
            if (modelClass.isAssignableFrom(StoreListViewModel::class.java)) {
                return StoreListViewModel(
                    storeRepository = storeRepository,
                ) as T
            }

            throw IllegalArgumentException(
                "알 수 없는 ViewModel입니다.",
            )
        }
    }
}