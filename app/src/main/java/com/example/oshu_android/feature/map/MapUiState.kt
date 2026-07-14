package com.example.oshu_android.feature.map

import com.example.oshu_android.data.store.StoreCardResponse

data class MapUiState(
    val searchQuery: String = "",
    val stores: List<StoreCardResponse> = emptyList(),
    val selectedStoreId: Long? = null,
    val isTimeSaleSelected: Boolean = false,
    val isHotDealSelected: Boolean = false,
    val isReservationSelected: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val filteredStores: List<StoreCardResponse>
        get() {
            val normalizedQuery =
                searchQuery.trim()

            return stores.filter { store ->
                val matchesQuery =
                    normalizedQuery.isBlank() ||
                            store.name.contains(
                                normalizedQuery,
                                ignoreCase = true,
                            ) ||
                            store.category.contains(
                                normalizedQuery,
                                ignoreCase = true,
                            ) ||
                            store.address.contains(
                                normalizedQuery,
                                ignoreCase = true,
                            )

                val matchesTimeSale =
                    !isTimeSaleSelected ||
                            store.timeSaleActive

                matchesQuery && matchesTimeSale
            }
        }

    val selectedStore: StoreCardResponse?
        get() =
            filteredStores.firstOrNull {
                it.storeId == selectedStoreId
            }
}