package com.example.oshu_android.feature.storelist

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oshu_android.R
import com.example.oshu_android.data.store.StoreCardResponse

private val ListBackground = Color(0xFFFFF8F9)
private val ListPrimary = Color(0xFFFF8A9C)
private val ListPrimaryLight = Color(0xFFFFE9ED)
private val ListBrown = Color(0xFF704B50)
private val ListBorder = Color(0xFFFFD6DE)
private val ListHint = Color(0xFF969198)

@Composable
fun StoreListRoute(
    viewModel: StoreListViewModel,
    stores: List<StoreCardResponse>,
    onMapClick: () -> Unit = {},
    onPromotionClick: () -> Unit = {},
    onStoreDetailClick: (Long) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(stores) {
        viewModel.updateStores(stores)
    }

    StoreListScreen(
        uiState = uiState,
        onQueryChanged = viewModel::onSearchQueryChanged,
        onCategorySelected = viewModel::onCategorySelected,
        onMapClick = onMapClick,
        onPromotionClick = onPromotionClick,
        onStoreDetailClick = onStoreDetailClick,
    )
}

@Composable
fun StoreListScreen(
    uiState: StoreListUiState,
    onQueryChanged: (String) -> Unit,
    onCategorySelected: (StoreListCategory) -> Unit,
    onMapClick: () -> Unit,
    onPromotionClick: () -> Unit,
    onStoreDetailClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ListBackground,
        bottomBar = {
            StoreListBottomBar(
                onMapClick = onMapClick,
                onPromotionClick = onPromotionClick,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            StoreListHeader()

            StoreListSearchField(
                value = uiState.searchQuery,
                onValueChange = onQueryChanged,
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 14.dp,
                ),
            )

            StoreCategoryTabs(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.padding(
                    top = 18.dp,
                ),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 14.dp,
                    bottom = 20.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                items(
                    items = uiState.filteredStores,
                    key = { store ->
                        store.storeId
                    },
                ) { store ->
                    StoreListCard(
                        store = store,
                        onClick = {
                            onStoreDetailClick(store.storeId)
                        },
                    )
                }

                if (uiState.filteredStores.isEmpty()) {
                    item {
                        EmptyStoreList()
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreListHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFF2F5),
    ) {
        Text(
            text = "OSHU",
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    start = 24.dp,
                    top = 18.dp,
                    bottom = 18.dp,
                ),
            color = ListPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun StoreListSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = BorderStroke(
            width = 1.dp,
            color = ListBorder,
        ),
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 18.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_search,
                ),
                contentDescription = "검색",
                tint = ListPrimary,
                modifier = Modifier.size(23.dp),
            )

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = 14.dp,
                    ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = ListBrown,
                    fontSize = 16.sp,
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isBlank()) {
                            Text(
                                text = "지역 혜택 검색...",
                                color = ListHint,
                                fontSize = 16.sp,
                            )
                        }

                        innerTextField()
                    }
                },
            )
        }
    }
}

@Composable
private fun StoreCategoryTabs(
    selectedCategory: StoreListCategory,
    onCategorySelected: (StoreListCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            items = StoreListCategory.entries,
        ) { category ->
            val selected = category == selectedCategory

            Surface(
                modifier = Modifier
                    .height(46.dp)
                    .clickable {
                        onCategorySelected(category)
                    },
                shape = RoundedCornerShape(23.dp),
                color = if (selected) ListPrimary else Color.White,
                border = if (selected) {
                    null
                } else {
                    BorderStroke(
                        width = 1.dp,
                        color = ListBorder,
                    )
                },
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = 19.dp,
                    ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = category.label,
                        color = if (selected) Color.White else ListBrown,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun StoreListCard(
    store: StoreCardResponse,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color(0xFFFFF7F8),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = ListBorder,
        ),
    ) {
        Column {
            StoreImagePlaceholder(
                category = store.category,
            )

            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = store.name,
                    color = Color(0xFF222222),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(
                    modifier = Modifier.height(8.dp),
                )

                Text(
                    text = store.address.ifBlank {
                        store.category
                    },
                    color = ListPrimary,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(
                    modifier = Modifier.height(12.dp),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StoreListTag(
                        text = crowdLabel(
                            store.crowdLevel,
                        ),
                    )

                    if (store.timeSaleActive) {
                        StoreListTag(
                            text = "타임 세일",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreImagePlaceholder(
    category: String,
) {
    val backgroundColor = when (
        category.trim().replace(" ", "").lowercase()
    ) {
        "카페", "cafe", "coffee" -> Color(0xFFD8B18B)
        "음식점", "식당", "restaurant", "food" -> Color(0xFFE7A17A)
        "마트", "mart" -> Color(0xFFB7D6B8)
        "베이커리", "bakery" -> Color(0xFFE8BC8E)
        else -> Color(0xFFDCC8B4)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                ),
            )
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(
                categoryMarkerResource(category),
            ),
            contentDescription = "매장 이미지",
            tint = Color.White,
            modifier = Modifier.size(62.dp),
        )
    }
}

@Composable
private fun StoreListTag(
    text: String,
) {
    Surface(
        color = ListPrimaryLight,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 5.dp,
            ),
            color = ListPrimary,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun EmptyStoreList() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 90.dp,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "조건에 맞는 매장이 없습니다.",
            color = ListHint,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun StoreListBottomBar(
    onMapClick: () -> Unit,
    onPromotionClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 48.dp,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StoreListNavigationItem(
                label = "지도",
                selected = false,
                selectedIcon = R.drawable.ic_map_fill,
                unselectedIcon = R.drawable.ic_map,
                onClick = onMapClick,
            )

            StoreListNavigationItem(
                label = "목록",
                selected = true,
                selectedIcon = R.drawable.ic_inventory_fill,
                unselectedIcon = R.drawable.ic_inventory,
                onClick = {},
            )

            StoreListNavigationItem(
                label = "프로모션",
                selected = false,
                selectedIcon = R.drawable.ic_promotion_fill,
                unselectedIcon = R.drawable.ic_promotion,
                onClick = onPromotionClick,
                showBadge = true,
            )
        }
    }
}

@Composable
private fun StoreListNavigationItem(
    label: String,
    selected: Boolean,
    @DrawableRes selectedIcon: Int,
    @DrawableRes unselectedIcon: Int,
    onClick: () -> Unit,
    showBadge: Boolean = false,
) {
    Box(
        modifier = Modifier
            .width(76.dp)
            .height(70.dp)
            .clip(
                RoundedCornerShape(16.dp),
            )
            .background(
                if (selected) {
                    ListPrimary
                } else {
                    Color.Transparent
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                Icon(
                    painter = painterResource(
                        if (selected) {
                            selectedIcon
                        } else {
                            unselectedIcon
                        },
                    ),
                    contentDescription = label,
                    tint = if (selected) Color.White else ListBrown,
                    modifier = Modifier.size(26.dp),
                )

                if (showBadge && !selected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(8.dp)
                            .background(
                                color = ListPrimary,
                                shape = CircleShape,
                            ),
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp),
            )

            Text(
                text = label,
                color = if (selected) Color.White else ListBrown,
                fontSize = 12.sp,
            )
        }
    }
}

@DrawableRes
private fun categoryMarkerResource(
    category: String,
): Int {
    return when (
        category.trim().replace(" ", "").lowercase()
    ) {
        "베이커리", "bakery" -> R.drawable.ic_marker_bakery
        "음식점", "식당", "restaurant", "food" -> R.drawable.ic_marker_food_market
        "카페", "cafe", "coffee" -> R.drawable.ic_marker_cafe
        "마트", "mart" -> R.drawable.ic_marker_mart
        else -> R.drawable.ic_marker_marketplace
    }
}