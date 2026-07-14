package com.example.oshu_android.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oshu_android.data.store.MapStoreResult
import com.example.oshu_android.data.store.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val storeRepository: StoreRepository,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(MapUiState())

    val uiState: StateFlow<MapUiState> =
        _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        if (_uiState.value.isLoading) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            when (
                val result =
                    storeRepository.getMapStores(
                        latitude =
                            INITIAL_LATITUDE,
                        longitude =
                            INITIAL_LONGITUDE,
                        radius =
                            INITIAL_RADIUS,
                    )
            ) {
                is MapStoreResult.Success -> {
                    _uiState.update {
                        it.copy(
                            stores = result.stores,
                            selectedStoreId = null,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }

                is MapStoreResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                result.message,
                        )
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(
        query: String,
    ) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                selectedStoreId = null,
            )
        }
    }

    fun onTimeSaleClick() {
        _uiState.update {
            it.copy(
                isTimeSaleSelected =
                    !it.isTimeSaleSelected,
                selectedStoreId = null,
            )
        }
    }

    fun onHotDealClick() {
        _uiState.update {
            it.copy(
                isHotDealSelected =
                    !it.isHotDealSelected,
            )
        }
    }

    fun onStoreClick(
        storeId: Long,
    ) {
        _uiState.update {
            it.copy(
                selectedStoreId = storeId,
            )
        }
    }

    fun onMapClick() {
        _uiState.update {
            it.copy(
                selectedStoreId = null,
            )
        }
    }

    fun onMapError(
        message: String,
    ) {
        _uiState.update {
            it.copy(
                errorMessage = message,
            )
        }
    }

    fun onErrorMessageShown() {
        _uiState.update {
            it.copy(
                errorMessage = null,
            )
        }
    }

    class Factory(
        private val storeRepository:
        StoreRepository,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(
            modelClass: Class<T>,
        ): T {
            if (
                modelClass.isAssignableFrom(
                    MapViewModel::class.java,
                )
            ) {
                @Suppress("UNCHECKED_CAST")
                return MapViewModel(
                    storeRepository =
                        storeRepository,
                ) as T
            }

            throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}",
            )
        }
    }

    companion object {
        const val INITIAL_LATITUDE =
            36.3622

        const val INITIAL_LONGITUDE =
            127.3449

        const val INITIAL_RADIUS =
            1500
    }
}
