package com.example.oshu_android.feature.promotion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oshu_android.R
import com.example.oshu_android.feature.common.MainBottomNavigation
import com.example.oshu_android.feature.common.MainDestination

private val PromotionBackground = Color(0xFFFFF8F9)
private val PromotionPrimary = Color(0xFFFF8A9C)
private val PromotionPrimaryLight = Color(0xFFFFE9ED)
private val PromotionBorder = Color(0xFFFFD6DE)
private val PromotionBrown = Color(0xFF704B50)

@Composable
fun PromotionRoute(
    viewModel: PromotionViewModel,
    onMapClick: () -> Unit = {},
    onListClick: () -> Unit = {},
    onPromotionClick: (Long) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    PromotionScreen(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onMapClick = onMapClick,
        onListClick = onListClick,
        onPromotionClick = onPromotionClick,
    )
}

@Composable
fun PromotionScreen(
    uiState: PromotionUiState,
    onCategorySelected: (PromotionCategory) -> Unit,
    onMapClick: () -> Unit,
    onListClick: () -> Unit,
    onPromotionClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val promotions = uiState.filteredPromotions
    val primaryPromotion = promotions.firstOrNull()
    val secondaryPromotions = promotions.drop(1).take(2)
    val remainingPromotions = promotions.drop(3)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PromotionBackground,
        bottomBar = {
            MainBottomNavigation(
                selectedDestination = MainDestination.PROMOTION,
                onDestinationSelected = { destination ->
                    when (destination) {
                        MainDestination.MAP -> onMapClick()
                        MainDestination.STORE_LIST -> onListClick()
                        MainDestination.PROMOTION -> Unit
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 22.dp,
                end = 22.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                PromotionHeader()
            }

            item {
                PromotionHero(
                    onClick = {
                        primaryPromotion?.let { promotion ->
                            onPromotionClick(promotion.id)
                        }
                    },
                )
            }

            item {
                PromotionCategoryTabs(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected,
                )
            }

            primaryPromotion?.let { promotion ->
                item {
                    PromotionLargeCard(
                        promotion = promotion,
                        onClick = {
                            onPromotionClick(promotion.id)
                        },
                    )
                }
            }

            if (secondaryPromotions.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        secondaryPromotions.forEach { promotion ->
                            PromotionSmallCard(
                                promotion = promotion,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onPromotionClick(promotion.id)
                                },
                            )
                        }

                        if (secondaryPromotions.size == 1) {
                            Spacer(
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            remainingPromotions.forEach { promotion ->
                item {
                    PromotionHorizontalCard(
                        promotion = promotion,
                        onClick = {
                            onPromotionClick(promotion.id)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PromotionHeader() {
    Text(
        text = "OSHU",
        modifier = Modifier
            .statusBarsPadding()
            .padding(
                top = 18.dp,
                bottom = 8.dp,
            ),
        color = PromotionPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
    )
}

@Composable
private fun PromotionHero(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(208.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF7D4B35),
                        Color(0xFFC98D5A),
                        Color(0xFFF4C38D),
                    ),
                ),
            )
            .clickable(onClick = onClick),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_hot_deal),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.28f),
            modifier = Modifier
                .align(Alignment.Center)
                .size(112.dp),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp),
        ) {
            Surface(
                color = PromotionPrimary,
                shape = RoundedCornerShape(7.dp),
            ) {
                Text(
                    text = "진행 중",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp,
                    ),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp),
            )

            Text(
                text = "우리 동네 핫딜",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "유성구의 가장 신선한 프로모션",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun PromotionCategoryTabs(
    selectedCategory: PromotionCategory,
    onCategorySelected: (PromotionCategory) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(PromotionCategory.entries) { category ->
            val selected = category == selectedCategory

            Surface(
                modifier = Modifier
                    .height(46.dp)
                    .clickable {
                        onCategorySelected(category)
                    },
                shape = RoundedCornerShape(23.dp),
                color = if (selected) PromotionPrimary else Color.White,
                border = if (selected) {
                    null
                } else {
                    BorderStroke(
                        width = 1.dp,
                        color = PromotionBorder,
                    )
                },
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                    ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = category.label,
                        color = if (selected) Color.White else PromotionBrown,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun PromotionLargeCard(
    promotion: PromotionItem,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column {
            PromotionVisual(
                promotion = promotion,
                height = 240.dp,
            )

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = promotion.title,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF222222),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                promotion.discountText?.let { text ->
                    Surface(
                        color = PromotionPrimaryLight,
                        shape = RoundedCornerShape(7.dp),
                    ) {
                        Text(
                            text = text,
                            modifier = Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 6.dp,
                            ),
                            color = PromotionPrimary,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromotionSmallCard(
    promotion: PromotionItem,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
    ) {
        Column {
            PromotionVisual(
                promotion = promotion,
                height = 126.dp,
            )

            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                Text(
                    text = promotion.title,
                    color = Color(0xFF222222),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(
                    modifier = Modifier.height(4.dp),
                )

                Text(
                    text = promotion.subtitle,
                    color = PromotionBrown,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun PromotionHorizontalCard(
    promotion: PromotionItem,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(promotion.colorStart),
                                Color(promotion.colorEnd),
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_hot_deal),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp),
                )
            }

            Spacer(
                modifier = Modifier.size(14.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = promotion.badge,
                    color = PromotionPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(
                    modifier = Modifier.height(4.dp),
                )

                Text(
                    text = promotion.title,
                    color = Color(0xFF222222),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = promotion.subtitle,
                    color = PromotionBrown,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            promotion.discountText?.let { text ->
                Surface(
                    color = PromotionPrimary,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = text,
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 7.dp,
                        ),
                        color = Color.White,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun PromotionVisual(
    promotion: PromotionItem,
    height: Dp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                ),
            )
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(promotion.colorStart),
                        Color(promotion.colorEnd),
                    ),
                ),
            ),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_hot_deal),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.74f),
            modifier = Modifier
                .align(Alignment.Center)
                .size(
                    if (height > 180.dp) {
                        78.dp
                    } else {
                        46.dp
                    },
                ),
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            color = PromotionPrimary,
            shape = RoundedCornerShape(7.dp),
        ) {
            Text(
                text = promotion.badge,
                modifier = Modifier.padding(
                    horizontal = 9.dp,
                    vertical = 6.dp,
                ),
                color = Color.White,
                fontSize = 12.sp,
            )
        }
    }
}