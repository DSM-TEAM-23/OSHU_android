package com.example.oshu_android.feature.map

import com.example.oshu_android.data.store.StoreCardResponse

data class MapUiState(
    val isLoading: Boolean = false,
    val stores: List<StoreCardResponse> =
        emptyList(),
    val selectedStoreId: Long? = null,
    val searchQuery: String = "",
    val timeSaleOnly: Boolean = false,
    val hotPlaceOnly: Boolean = false,
    val errorMessage: String? = null,
) {
    val visibleStores:
            List<StoreCardResponse>
        get() {
            val keyword =
                searchQuery.trim()

            return stores.filter { store ->
                val matchesKeyword =
                    keyword.isBlank() ||
                            store.name.contains(
                                other = keyword,
                                ignoreCase = true,
                            ) ||
                            store.category.contains(
                                other = keyword,
                                ignoreCase = true,
                            ) ||
                            store.address.contains(
                                other = keyword,
                                ignoreCase = true,
                            )

                val crowdLevel =
                    store.crowdLevel
                        ?.uppercase()

                val matchesHotPlace =
                    !hotPlaceOnly ||
                            crowdLevel == "BUSY" ||
                            crowdLevel == "VERY_BUSY"

                matchesKeyword &&
                        matchesHotPlace
            }
        }

    val selectedStore:
            StoreCardResponse?
        get() {
            return stores.firstOrNull {
                it.storeId == selectedStoreId
            }
        }
}