package com.example.oshu_android.feature.storelist

import com.example.oshu_android.data.store.StoreCardResponse

data class StoreListUiState(
    val stores: List<StoreCardResponse> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: StoreListCategory = StoreListCategory.ALL,
    val activeDiscountLabels: Map<Long, String> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val filteredStores: List<StoreCardResponse>
        get() = stores.filter { store ->
            matchesQuery(store)
        }

    private fun matchesQuery(
        store: StoreCardResponse,
    ): Boolean {
        val query = searchQuery.trim()

        if (query.isBlank()) {
            return true
        }

        return store.name.contains(
            other = query,
            ignoreCase = true,
        ) ||
                store.address.contains(
                    other = query,
                    ignoreCase = true,
                ) ||
                store.category.contains(
                    other = query,
                    ignoreCase = true,
                )
    }
}

enum class StoreListCategory(
    val label: String,
    val apiCategory: String?,
) {
    ALL(
        label = "전체",
        apiCategory = null,
    ),
    CAFE(
        label = "카페",
        apiCategory = "카페",
    ),
    RESTAURANT(
        label = "음식점",
        apiCategory = "음식점",
    ),
    MART(
        label = "마트",
        apiCategory = "마트",
    ),
    BAKERY(
        label = "베이커리",
        apiCategory = "베이커리",
    );
}

fun crowdLabel(
    level: String?,
): String {
    return when (level?.uppercase()) {
        "RELAXED" -> "여유"
        "NORMAL" -> "보통"
        "BUSY" -> "혼잡"
        "VERY_BUSY" -> "매우 혼잡"
        else -> "여유"
    }
}
