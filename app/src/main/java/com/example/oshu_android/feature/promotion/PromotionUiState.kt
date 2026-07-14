package com.example.oshu_android.feature.promotion

import com.example.oshu_android.data.store.PromotionResponse

data class PromotionUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCategory: PromotionCategory = PromotionCategory.ALL,
    val promotions: List<PromotionItem> = emptyList(),
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
    ALL("전체 딜"),
    RESTAURANT("음식점"),
    CAFE("카페"),
    GROCERY("식료품");

    companion object {
        fun fromType(
            type: String?,
        ): PromotionCategory {
            return when (type?.trim()?.uppercase()) {
                "RESTAURANT",
                "FOOD",
                "DINING",
                "음식점",
                "식당" -> RESTAURANT

                "CAFE",
                "COFFEE",
                "BAKERY",
                "카페",
                "베이커리" -> CAFE

                "GROCERY",
                "MART",
                "MARKET",
                "식료품",
                "마트" -> GROCERY

                else -> ALL
            }
        }
    }
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
    val promotionType = type.orEmpty()

    return PromotionItem(
        id = promotionId,
        storeId = storeId,
        storeName = storeName.orEmpty(),
        title = title.orEmpty().ifBlank {
            "프로모션"
        },
        content = content.orEmpty(),
        type = promotionType,
        imageUrl = imageUrl?.takeIf {
            it.isNotBlank()
        },
        startAt = startAt,
        endAt = endAt,
        status = status.orEmpty(),
        category = PromotionCategory.fromType(
            promotionType,
        ),
    )
}

fun PromotionItem.badgeLabel(): String {
    return when (type.trim().uppercase()) {
        "TIME_SALE" -> "타임 세일"
        "SALE" -> "할인 행사"
        "EVENT" -> "이벤트"
        "COUPON" -> "쿠폰 혜택"
        else -> {
            if (status.isNotBlank()) {
                status
            } else {
                "진행 중"
            }
        }
    }
}

fun PromotionItem.periodLabel(): String {
    val start = startAt
        ?.replace("T", " ")
        ?.take(16)

    val end = endAt
        ?.replace("T", " ")
        ?.take(16)

    return listOfNotNull(
        start,
        end,
    ).joinToString(" ~ ")
}