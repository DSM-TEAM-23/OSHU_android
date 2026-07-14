package com.example.oshu_android.feature.promotion

data class PromotionUiState(
    val selectedCategory: PromotionCategory = PromotionCategory.ALL,
    val promotions: List<PromotionItem> = samplePromotions,
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
    GROCERY("식료품"),
}

data class PromotionItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val badge: String,
    val discountText: String?,
    val category: PromotionCategory,
    val colorStart: Long,
    val colorEnd: Long,
)

val samplePromotions = listOf(
    PromotionItem(
        id = 1L,
        title = "오늘의 빵: 50% 할인",
        subtitle = "오슈 베이커리",
        badge = "마감 세일",
        discountText = "00:42:15",
        category = PromotionCategory.CAFE,
        colorStart = 0xFFD89154,
        colorEnd = 0xFFF5D0A4,
    ),
    PromotionItem(
        id = 2L,
        title = "점심 특선",
        subtitle = "비스트로 서울",
        badge = "편안한 분위기",
        discountText = null,
        category = PromotionCategory.RESTAURANT,
        colorStart = 0xFFB86F45,
        colorEnd = 0xFFF1C28D,
    ),
    PromotionItem(
        id = 3L,
        title = "무료 쿠키 증정",
        subtitle = "어반 로스트",
        badge = "그랜드 오픈",
        discountText = null,
        category = PromotionCategory.CAFE,
        colorStart = 0xFFAF7B58,
        colorEnd = 0xFFE9C597,
    ),
    PromotionItem(
        id = 4L,
        title = "과일 바구니 세트",
        subtitle = "싱싱 마트",
        badge = "반짝 세일",
        discountText = "-30%",
        category = PromotionCategory.GROCERY,
        colorStart = 0xFFC5A45C,
        colorEnd = 0xFFF2DB93,
    ),
    PromotionItem(
        id = 5L,
        title = "심야 콤보",
        subtitle = "유성 왕조",
        badge = "식사 추천",
        discountText = null,
        category = PromotionCategory.RESTAURANT,
        colorStart = 0xFF9C6046,
        colorEnd = 0xFFDBA35C,
    ),
    PromotionItem(
        id = 6L,
        title = "주말 마켓",
        subtitle = "커뮤니티 광장",
        badge = "주말 한정",
        discountText = null,
        category = PromotionCategory.GROCERY,
        colorStart = 0xFF7FAD7D,
        colorEnd = 0xFFD5E4A8,
    ),
)