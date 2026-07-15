package com.example.oshu_android.feature.promotion

import com.example.oshu_android.data.store.PromotionResponse

data class PromotionUiState(
    val selectedCategory: PromotionCategory = PromotionCategory.ALL,
    val promotions: List<PromotionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val filteredPromotions: List<PromotionItem>
        get() = promotions.filter { promotion ->
            selectedCategory == PromotionCategory.ALL ||
                    promotion.category == selectedCategory
        }
}

enum class PromotionCategory(
    val label: String,
) {
    ALL("전체"),
    RESTAURANT("음식점"),
    CAFE("카페"),
    GROCERY("식료품"),
}

data class PromotionItem(
    val id: Long,
    val storeId: Long,
    val storeName: String,
    val title: String,
    val content: String,
    val type: String,
    val imageUrl: String?,
    val startAt: String?,
    val endAt: String?,
    val status: String,
    val category: PromotionCategory,
)

fun PromotionResponse.toPromotionItem(): PromotionItem {
    return PromotionItem(
        id = promotionId,
        storeId = storeId,
        storeName = storeName,
        title = title,
        content = content,
        type = type,
        imageUrl = imageUrl,
        startAt = startAt,
        endAt = endAt,
        status = status,
        category = type.toPromotionCategory(),
    )
}

private fun String.toPromotionCategory(): PromotionCategory {
    return when (uppercase()) {
        "CAFE", "BAKERY", "COFFEE" -> PromotionCategory.CAFE
        "RESTAURANT", "FOOD", "DINING" -> PromotionCategory.RESTAURANT
        else -> PromotionCategory.GROCERY
    }
}