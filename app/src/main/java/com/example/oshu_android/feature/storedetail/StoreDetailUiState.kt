package com.example.oshu_android.feature.storedetail

import com.example.oshu_android.data.store.StoreDetailResponse

data class StoreDetailUiState(
    val store: StoreDetailResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
