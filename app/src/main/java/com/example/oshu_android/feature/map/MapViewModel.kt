package com.example.oshu_android.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oshu_android.data.store.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val storeRepository:
    StoreRepository,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            MapUiState()
        )

    val uiState:
            StateFlow<MapUiState> =
        _uiState.asStateFlow()

    private var currentLatitude =
        INITIAL_LATITUDE

    private var currentLongitude =
        INITIAL_LONGITUDE

    init {
        refresh()
    }

    fun refresh() {
        loadStores(
            latitude = currentLatitude,
            longitude = currentLongitude,
        )
    }

    fun updateMapCenter(
        latitude: Double,
        longitude: Double,
    ) {
        currentLatitude = latitude
        currentLongitude = longitude

        loadStores(
            latitude = latitude,
            longitude = longitude,
        )
    }

    fun onSearchQueryChange(
        query: String,
    ) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                selectedStoreId = null,
            )
        }
    }

    fun onTimeSaleFilterClick() {
        val enabled =
            !_uiState.value.timeSaleOnly

        _uiState.update {
            it.copy(
                timeSaleOnly = enabled,
                selectedStoreId = null,
            )
        }

        refresh()
    }

    fun onHotPlaceFilterClick() {
        _uiState.update {
            it.copy(
                hotPlaceOnly =
                    !it.hotPlaceOnly,
                selectedStoreId = null,
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
                isLoading = false,
                errorMessage = message,
            )
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                errorMessage = null,
            )
        }
    }

    private fun loadStores(
        latitude: Double,
        longitude: Double,
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            val result =
                storeRepository.getMapStores(
                    latitude = latitude,
                    longitude = longitude,
                    radius = DEFAULT_RADIUS,
                    timeSaleOnly =
                        _uiState.value
                            .timeSaleOnly,
                )

            result.fold(
                onSuccess = { stores ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            stores = stores,
                            selectedStoreId = null,
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                throwable.message
                                    ?: "가게 정보를 불러오지 못했습니다.",
                        )
                    }
                },
            )
        }
    }

    class Factory(
        private val storeRepository:
        StoreRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
        ): T {
            require(
                modelClass.isAssignableFrom(
                    MapViewModel::class.java
                )
            )

            return MapViewModel(
                storeRepository =
                    storeRepository,
            ) as T
        }
    }

    companion object {
        const val INITIAL_LATITUDE =
            36.3624

        const val INITIAL_LONGITUDE =
            127.3445

        const val DEFAULT_RADIUS =
            1500
    }
}