package com.example.oshu_android.feature.map

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oshu_android.R
import com.example.oshu_android.data.store.StoreCardResponse
import kotlinx.coroutines.delay

private val MapBackground =
    Color(0xFFFFF7F8)

private val MapPrimary =
    Color(0xFFFF829B)

private val MapPrimaryLight =
    Color(0xFFFFDDE4)

private val MapBrown =
    Color(0xFF6E474A)

private val MapBorder =
    Color(0xFFFFCBD5)

private val MapHint =
    Color(0xFF8C8A91)

@Composable
fun MapRoute(
    viewModel: MapViewModel,
    onListClick: () -> Unit = {},
    onPromotionClick: () -> Unit = {},
) {
    val uiState by
    viewModel.uiState.collectAsState()

    MapScreen(
        uiState = uiState,
        onSearchQueryChanged =
            viewModel::onSearchQueryChanged,
        onTimeSaleClick =
            viewModel::onTimeSaleClick,
        onHotDealClick =
            viewModel::onHotDealClick,
        onReservationClick =
            viewModel::onReservationClick,
        onStoreClick =
            viewModel::onStoreClick,
        onMapClick =
            viewModel::onMapClick,
        onMapError =
            viewModel::onMapError,
        onRefresh =
            viewModel::refresh,
        onErrorMessageShown =
            viewModel::onErrorMessageShown,
        onListClick = onListClick,
        onPromotionClick = onPromotionClick,
    )
}

@Composable
fun MapScreen(
    uiState: MapUiState,
    onSearchQueryChanged: (String) -> Unit,
    onTimeSaleClick: () -> Unit,
    onHotDealClick: () -> Unit,
    onReservationClick: () -> Unit,
    onStoreClick: (Long) -> Unit,
    onMapClick: () -> Unit,
    onMapError: (String) -> Unit,
    onRefresh: () -> Unit,
    onErrorMessageShown: () -> Unit,
    onListClick: () -> Unit,
    onPromotionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MapBackground,
        bottomBar = {
            MapBottomBar(
                selectedItem =
                    MapBottomDestination.MAP,
                onMapClick = {},
                onListClick = onListClick,
                onPromotionClick =
                    onPromotionClick,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                MapHeader(
                    query = uiState.searchQuery,
                    onQueryChanged =
                        onSearchQueryChanged,
                    isTimeSaleSelected =
                        uiState.isTimeSaleSelected,
                    isHotDealSelected =
                        uiState.isHotDealSelected,
                    isReservationSelected =
                        uiState.isReservationSelected,
                    onTimeSaleClick =
                        onTimeSaleClick,
                    onHotDealClick =
                        onHotDealClick,
                    onReservationClick =
                        onReservationClick,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    KakaoMapView(
                        stores =
                            uiState.filteredStores,
                        selectedStoreId =
                            uiState.selectedStoreId,
                        onStoreClick =
                            onStoreClick,
                        onMapClick =
                            onMapClick,
                        onMapError =
                            onMapError,
                        modifier =
                            Modifier.fillMaxSize(),
                    )

                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(
                                    Alignment.TopCenter,
                                )
                                .padding(top = 20.dp)
                                .size(28.dp),
                            color = MapPrimary,
                            strokeWidth = 3.dp,
                        )
                    }

                    uiState.selectedStore?.let {
                        SelectedStoreCard(
                            store = it,
                            modifier = Modifier
                                .align(
                                    Alignment.BottomCenter,
                                )
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 18.dp,
                                ),
                        )
                    }
                }
            }

            uiState.errorMessage?.let { message ->
                MapErrorMessage(
                    message = message,
                    onRefresh = onRefresh,
                    onShown =
                        onErrorMessageShown,
                    modifier = Modifier
                        .align(
                            Alignment.BottomCenter,
                        )
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 18.dp,
                        ),
                )
            }
        }
    }
}

@Composable
private fun MapHeader(
    query: String,
    onQueryChanged: (String) -> Unit,
    isTimeSaleSelected: Boolean,
    isHotDealSelected: Boolean,
    isReservationSelected: Boolean,
    onTimeSaleClick: () -> Unit,
    onHotDealClick: () -> Unit,
    onReservationClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(
                    bottomStart = 22.dp,
                    bottomEnd = 22.dp,
                ),
            ),
        color = MapBackground,
        shape = RoundedCornerShape(
            bottomStart = 22.dp,
            bottomEnd = 22.dp,
        ),
    ) {
        Column(
            modifier = Modifier.padding(
                start = 24.dp,
                end = 24.dp,
                top = 28.dp,
                bottom = 20.dp,
            ),
        ) {
            Text(
                text = "OSHU",
                color = MapPrimary,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(
                modifier = Modifier.height(28.dp),
            )

            MapSearchField(
                value = query,
                onValueChange = onQueryChanged,
            )

            Spacer(
                modifier = Modifier.height(20.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),
            ) {
                MapFilterChip(
                    text = "타임 세일",
                    selected =
                        isTimeSaleSelected,
                    onClick =
                        onTimeSaleClick,
                )

                MapFilterChip(
                    text = "핫딜",
                    icon = R.drawable.ic_hot_deal,
                    selected =
                        isHotDealSelected,
                    onClick =
                        onHotDealClick,
                )

                MapFilterChip(
                    text = "예약 가능",
                    icon = R.drawable.ic_reservation,
                    selected =
                        isReservationSelected,
                    onClick =
                        onReservationClick,
                )
            }
        }
    }
}

@Composable
private fun MapSearchField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .border(
                width = 1.dp,
                color = MapBorder,
                shape = RoundedCornerShape(29.dp),
            ),
        shape = RoundedCornerShape(29.dp),
        color = Color.White,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_search,
                ),
                contentDescription = "검색",
                tint = MapPrimary,
                modifier = Modifier.size(27.dp),
            )

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                singleLine = true,
                textStyle =
                    MaterialTheme.typography.bodyLarge
                        .copy(
                            color = MapBrown,
                            fontSize = 17.sp,
                        ),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isBlank()) {
                            Text(
                                text = "지역 혜택 검색...",
                                color = MapHint,
                                fontSize = 17.sp,
                            )
                        }

                        innerTextField()
                    }
                },
            )

            Icon(
                painter = painterResource(
                    R.drawable.ic_my_location,
                ),
                contentDescription = "내 위치",
                tint = MapPrimary,
                modifier = Modifier.size(27.dp),
            )
        }
    }
}

@Composable
private fun MapFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    @DrawableRes icon: Int? = null,
) {
    Surface(
        modifier = Modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color =
            if (selected) {
                MapPrimary
            } else {
                Color.White
            },
        border =
            if (selected) {
                null
            } else {
                BorderStroke(
                    width = 1.dp,
                    color = MapBorder,
                )
            },
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
            ),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.Center,
        ) {
            if (icon != null) {
                Icon(
                    painter =
                        painterResource(icon),
                    contentDescription = null,
                    tint =
                        if (selected) {
                            Color.White
                        } else if (
                            icon ==
                            R.drawable.ic_hot_deal
                        ) {
                            MapPrimary
                        } else {
                            MapBrown
                        },
                    modifier =
                        Modifier.size(20.dp),
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp),
                )
            }

            Text(
                text = text,
                color =
                    if (selected) {
                        Color.White
                    } else {
                        MapBrown
                    },
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SelectedStoreCard(
    store: StoreCardResponse,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MapBorder,
        ),
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = MapPrimaryLight,
                        shape =
                            RoundedCornerShape(10.dp),
                    ),
                contentAlignment =
                    Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(
                        markerResource(
                            category =
                                store.category,
                        ),
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier =
                        Modifier.size(44.dp),
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text =
                        store.name.ifBlank {
                            "가게 정보"
                        },
                    color = MapBrown,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis,
                )

                Spacer(
                    modifier = Modifier.height(5.dp),
                )

                Text(
                    text =
                        store.address.ifBlank {
                            store.category
                        },
                    color = MapBrown.copy(
                        alpha = 0.72f,
                    ),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis,
                )

                Spacer(
                    modifier = Modifier.height(8.dp),
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                ) {
                    if (store.timeSaleActive) {
                        StoreTag(
                            text = "타임 세일",
                        )
                    }

                    store.crowdLevel?.let {
                        if (it.isNotBlank()) {
                            StoreTag(
                                text =
                                    crowdLabel(it),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreTag(
    text: String,
) {
    Surface(
        color = MapPrimaryLight,
        shape = RoundedCornerShape(5.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp,
            ),
            color = MapPrimary,
            fontSize = 11.sp,
        )
    }
}

private enum class MapBottomDestination {
    MAP,
    LIST,
    PROMOTION,
}

@Composable
private fun MapBottomBar(
    selectedItem: MapBottomDestination,
    onMapClick: () -> Unit,
    onListClick: () -> Unit,
    onPromotionClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        color = MapBackground,
        shadowElevation = 5.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 54.dp),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            BottomNavigationItem(
                label = "지도",
                selected =
                    selectedItem ==
                            MapBottomDestination.MAP,
                selectedIcon =
                    R.drawable.ic_map_fill,
                unselectedIcon =
                    R.drawable.ic_map,
                onClick = onMapClick,
            )

            BottomNavigationItem(
                label = "목록",
                selected =
                    selectedItem ==
                            MapBottomDestination.LIST,
                selectedIcon =
                    R.drawable.ic_inventory_fill,
                unselectedIcon =
                    R.drawable.ic_inventory,
                onClick = onListClick,
            )

            BottomNavigationItem(
                label = "프로모션",
                selected =
                    selectedItem ==
                            MapBottomDestination.PROMOTION,
                selectedIcon =
                    R.drawable.ic_promotion_fill,
                unselectedIcon =
                    R.drawable.ic_promotion,
                showBadge = true,
                onClick = onPromotionClick,
            )
        }
    }
}

@Composable
private fun BottomNavigationItem(
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
            .height(72.dp)
            .background(
                color =
                    if (selected) {
                        MapPrimary
                    } else {
                        Color.Transparent
                    },
                shape =
                    RoundedCornerShape(15.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center,
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
                    tint =
                        if (selected) {
                            Color.White
                        } else {
                            MapBrown
                        },
                    modifier =
                        Modifier.size(27.dp),
                )

                if (
                    showBadge &&
                    !selected
                ) {
                    Box(
                        modifier = Modifier
                            .align(
                                Alignment.TopEnd,
                            )
                            .size(8.dp)
                            .background(
                                color = MapPrimary,
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
                color =
                    if (selected) {
                        Color.White
                    } else {
                        MapBrown
                    },
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun MapErrorMessage(
    message: String,
    onRefresh: () -> Unit,
    onShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(message) {
        delay(4000)
        onShown()
    }

    Surface(
        modifier = modifier.clickable {
            onRefresh()
            onShown()
        },
        color = Color(0xFFFFD8D5),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 5.dp,
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(
                horizontal = 22.dp,
                vertical = 15.dp,
            ),
            color = MapBrown,
            fontSize = 14.sp,
        )
    }
}

@DrawableRes
private fun markerResource(
    category: String,
): Int {
    return when (
        category
            .trim()
            .replace(" ", "")
            .lowercase()
    ) {
        "베이커리",
        "bakery" ->
            R.drawable.ic_marker_bakery

        "음식점",
        "식당",
        "restaurant",
        "food" ->
            R.drawable.ic_marker_food_market

        "카페",
        "cafe",
        "coffee" ->
            R.drawable.ic_marker_cafe

        "마트",
        "mart" ->
            R.drawable.ic_marker_mart

        "시장·식료품",
        "시장/식료품",
        "시장식료품",
        "식료품",
        "marketplace",
        "market" ->
            R.drawable.ic_marker_marketplace

        else ->
            R.drawable.ic_marker_marketplace
    }
}

private fun crowdLabel(
    level: String,
): String {
    return when (level.uppercase()) {
        "RELAXED" -> "쾌적함"
        "NORMAL" -> "보통"
        "BUSY" -> "혼잡"
        "VERY_BUSY" -> "매우 혼잡"
        else -> level
    }
}