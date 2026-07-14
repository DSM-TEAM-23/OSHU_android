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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.oshu_android.R
import com.example.oshu_android.feature.common.MainBottomNavigation
import com.example.oshu_android.feature.common.MainDestination
import com.example.oshu_android.ui.theme.OshuBorder
import com.example.oshu_android.ui.theme.OshuHint
import com.example.oshu_android.ui.theme.OshuPink
import com.example.oshu_android.ui.theme.OshuPinkLight
import com.example.oshu_android.ui.theme.OshuTextPrimary
import com.example.oshu_android.ui.theme.OshuTextSecondary
import com.example.oshu_android.ui.theme.OshuWhite

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
        onRefresh = viewModel::refresh,
        onMapClick = onMapClick,
        onListClick = onListClick,
        onPromotionClick = onPromotionClick,
    )
}

@Composable
fun PromotionScreen(
    uiState: PromotionUiState,
    onCategorySelected: (PromotionCategory) -> Unit,
    onRefresh: () -> Unit,
    onMapClick: () -> Unit,
    onListClick: () -> Unit,
    onPromotionClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val promotions = uiState.filteredPromotions
    val heroPromotion = promotions.firstOrNull()
    val featuredPromotion = promotions.getOrNull(1)
    val secondaryPromotions = promotions.drop(2).take(2)
    val remainingPromotions = promotions.drop(4)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OshuPinkLight,
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

            heroPromotion?.let { promotion ->
                item {
                    PromotionHero(
                        promotion = promotion,
                        onClick = {
                            onPromotionClick(promotion.id)
                        },
                    )
                }
            }

            item {
                PromotionCategoryTabs(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected,
                )
            }

            when {
                uiState.isLoading -> {
                    item {
                        LoadingContent()
                    }
                }

                uiState.errorMessage != null -> {
                    item {
                        ErrorContent(
                            message = uiState.errorMessage,
                            onRefresh = onRefresh,
                        )
                    }
                }

                promotions.isEmpty() -> {
                    item {
                        EmptyContent()
                    }
                }

                else -> {
                    featuredPromotion?.let { promotion ->
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
        color = OshuPink,
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
    )
}

@Composable
private fun PromotionHero(
    promotion: PromotionItem,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(208.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        PromotionImage(
            promotion = promotion,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.64f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp),
        ) {
            Surface(
                color = OshuPink,
                shape = RoundedCornerShape(7.dp),
            ) {
                Text(
                    text = promotion.badgeLabel(),
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp,
                    ),
                    color = OshuWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp),
            )

            Text(
                text = promotion.title,
                color = OshuWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = promotion.storeName.ifBlank {
                    promotion.content
                },
                color = OshuWhite.copy(alpha = 0.9f),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
        items(
            items = PromotionCategory.values().toList(),
        ) { category ->
            val selected = category == selectedCategory

            Surface(
                modifier = Modifier
                    .height(46.dp)
                    .clickable {
                        onCategorySelected(category)
                    },
                shape = RoundedCornerShape(23.dp),
                color = if (selected) OshuPink else OshuWhite,
                border = if (selected) {
                    null
                } else {
                    BorderStroke(
                        width = 1.dp,
                        color = OshuBorder,
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
                        color = if (selected) OshuWhite else OshuTextPrimary,
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
        color = OshuWhite,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column {
            PromotionImage(
                promotion = promotion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                ),
            )

            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = promotion.title,
                    color = OshuTextPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(
                    modifier = Modifier.height(6.dp),
                )

                Text(
                    text = promotion.content.ifBlank {
                        promotion.storeName
                    },
                    color = OshuTextSecondary,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                promotion.periodLabel()
                    .takeIf { it.isNotBlank() }
                    ?.let { period ->
                        Spacer(
                            modifier = Modifier.height(8.dp),
                        )

                        Text(
                            text = period,
                            color = OshuPink,
                            fontSize = 12.sp,
                        )
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
        color = OshuWhite,
        shape = RoundedCornerShape(14.dp),
    ) {
        Column {
            PromotionImage(
                promotion = promotion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp),
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                ),
            )

            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                Text(
                    text = promotion.title,
                    color = OshuTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(
                    modifier = Modifier.height(4.dp),
                )

                Text(
                    text = promotion.storeName,
                    color = OshuTextSecondary,
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
        color = OshuWhite,
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PromotionImage(
                promotion = promotion,
                modifier = Modifier.size(92.dp),
                shape = RoundedCornerShape(10.dp),
            )

            Spacer(
                modifier = Modifier.size(14.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = promotion.badgeLabel(),
                    color = OshuPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(
                    modifier = Modifier.height(4.dp),
                )

                Text(
                    text = promotion.title,
                    color = OshuTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = promotion.storeName.ifBlank {
                        promotion.content
                    },
                    color = OshuTextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun PromotionImage(
    promotion: PromotionItem,
    modifier: Modifier,
    shape: RoundedCornerShape,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFB87559),
                        Color(0xFFF2C78F),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (!promotion.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = promotion.imageUrl,
                contentDescription = promotion.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_hot_deal,
                ),
                contentDescription = null,
                tint = OshuWhite.copy(alpha = 0.74f),
                modifier = Modifier.size(46.dp),
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
            color = OshuPink,
            shape = RoundedCornerShape(7.dp),
        ) {
            Text(
                text = promotion.badgeLabel(),
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 5.dp,
                ),
                color = OshuWhite,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 90.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = OshuPink,
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRefresh: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRefresh),
        color = OshuWhite,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = OshuBorder,
        ),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = message,
                color = OshuTextSecondary,
                fontSize = 14.sp,
            )

            Spacer(
                modifier = Modifier.height(10.dp),
            )

            Text(
                text = "다시 시도",
                color = OshuPink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 90.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "진행 중인 프로모션이 없습니다.",
            color = OshuHint,
            fontSize = 15.sp,
        )
    }
}

private fun PromotionItem.badgeLabel(): String {
    return when (type.uppercase()) {
        "TIME_SALE" -> "마감 세일"
        "HOT_DEAL" -> "핫딜"
        "OPEN_EVENT" -> "그랜드 오픈"
        "EVENT" -> "진행 중"
        else -> "진행 중"
    }
}

private fun PromotionItem.periodLabel(): String {
    val start = startAt.orEmpty()
    val end = endAt.orEmpty()

    return when {
        start.isNotBlank() && end.isNotBlank() -> "$start ~ $end"
        end.isNotBlank() -> "종료 $end"
        start.isNotBlank() -> "시작 $start"
        else -> ""
    }
}