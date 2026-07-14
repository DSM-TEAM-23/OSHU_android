package com.example.oshu_android.feature.storelist

import com.example.oshu_android.data.store.StoreCardResponse

data class StoreListUiState(
    val stores: List<StoreCardResponse> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: StoreListCategory = StoreListCategory.ALL,
) {
    val filteredStores: List<StoreCardResponse>
        get() = stores.filter { store ->
            selectedCategory.matches(store.category) &&
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
) {
    ALL("전체"),
    CAFE("카페"),
    RESTAURANT("음식점"),
    MART("마트"),
    BAKERY("베이커리");

    fun matches(
        category: String,
    ): Boolean {
        val normalized = category
            .trim()
            .replace(" ", "")
            .lowercase()

        return when (this) {
            ALL -> true

            CAFE -> normalized in setOf(
                "카페",
                "cafe",
                "coffee",
            )

            RESTAURANT -> normalized in setOf(
                "음식점",
                "식당",
                "restaurant",
                "food",
            )

            MART -> normalized in setOf(
                "마트",
                "mart",
            )

            BAKERY -> normalized in setOf(
                "베이커리",
                "bakery",
            )
        }
    }
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