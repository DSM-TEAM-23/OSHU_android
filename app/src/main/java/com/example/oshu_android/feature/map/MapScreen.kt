package com.example.oshu_android.feature.map

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oshu_android.R
import com.example.oshu_android.data.store.StoreCardResponse
import com.example.oshu_android.data.store.StoreModule

@Composable
fun MapScreen() {
    val context =
        LocalContext.current

    val storeRepository =
        remember(context) {
            StoreModule
                .provideStoreRepository(
                    context = context,
                )
        }

    val mapViewModel:
            MapViewModel =
        viewModel(
            factory =
                MapViewModel.Factory(
                    storeRepository =
                        storeRepository,
                ),
        )

    val uiState by
    mapViewModel.uiState
        .collectAsStateWithLifecycle()

    MapScreenContent(
        uiState = uiState,
        onSearchQueryChange =
            mapViewModel::onSearchQueryChange,
        onSearch =
            mapViewModel::refresh,
        onRefresh =
            mapViewModel::refresh,
        onTimeSaleFilterClick =
            mapViewModel::onTimeSaleFilterClick,
        onHotPlaceFilterClick =
            mapViewModel::onHotPlaceFilterClick,
        onStoreClick =
            mapViewModel::onStoreClick,
        onMapClick =
            mapViewModel::onMapClick,
        onMapError =
            mapViewModel::onMapError,
        onDismissError =
            mapViewModel::clearError,
    )
}

@Composable
private fun MapScreenContent(
    uiState: MapUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    onTimeSaleFilterClick: () -> Unit,
    onHotPlaceFilterClick: () -> Unit,
    onStoreClick: (Long) -> Unit,
    onMapClick: () -> Unit,
    onMapError: (String) -> Unit,
    onDismissError: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                ),
    ) {
        KakaoMapView(
            stores =
                uiState.visibleStores,
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

        MapTopPanel(
            query = uiState.searchQuery,
            timeSaleSelected =
                uiState.timeSaleOnly,
            hotPlaceSelected =
                uiState.hotPlaceOnly,
            onQueryChange =
                onSearchQueryChange,
            onSearch = onSearch,
            onRefresh = onRefresh,
            onTimeSaleClick =
                onTimeSaleFilterClick,
            onHotPlaceClick =
                onHotPlaceFilterClick,
            modifier =
                Modifier.align(
                    Alignment.TopCenter
                ),
        )

        uiState.selectedStore?.let {
            SelectedStoreCard(
                store = it,
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .padding(
                            horizontal = 20.dp,
                            vertical = 98.dp,
                        ),
            )
        }

        MapBottomNavigation(
            modifier =
                Modifier.align(
                    Alignment.BottomCenter
                ),
        )

        if (uiState.isLoading) {
            Surface(
                shape = CircleShape,
                color =
                    MaterialTheme
                        .colorScheme
                        .surface
                        .copy(alpha = 0.94f),
                shadowElevation = 6.dp,
                modifier =
                    Modifier
                        .align(
                            Alignment.Center
                        )
                        .size(64.dp),
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        strokeWidth = 3.dp,
                        modifier =
                            Modifier.size(30.dp),
                    )
                }
            }
        }

        uiState.errorMessage?.let {
            Surface(
                onClick = onDismissError,
                color =
                    MaterialTheme
                        .colorScheme
                        .errorContainer,
                shape =
                    RoundedCornerShape(10.dp),
                shadowElevation = 4.dp,
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 106.dp,
                        ),
            ) {
                Text(
                    text = it,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onErrorContainer,
                    fontSize = 14.sp,
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 12.dp,
                        ),
                )
            }
        }
    }
}

@Composable
private fun MapTopPanel(
    query: String,
    timeSaleSelected: Boolean,
    hotPlaceSelected: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    onTimeSaleClick: () -> Unit,
    onHotPlaceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color =
            MaterialTheme
                .colorScheme
                .background,
        shadowElevation = 5.dp,
        shape =
            RoundedCornerShape(
                bottomStart = 18.dp,
                bottomEnd = 18.dp,
            ),
        modifier =
            modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
                Modifier
                    .statusBarsPadding()
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 18.dp,
                        bottom = 18.dp,
                    ),
        ) {
            Text(
                text = "OSHU",
                color =
                    MaterialTheme
                        .colorScheme
                        .primary,
                fontSize = 26.sp,
                fontWeight =
                    FontWeight.Bold,
            )

            Spacer(
                modifier =
                    Modifier.height(28.dp),
            )

            MapSearchBar(
                query = query,
                onQueryChange =
                    onQueryChange,
                onSearch = onSearch,
                onRefresh = onRefresh,
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp),
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),
                modifier =
                    Modifier.fillMaxWidth(),
            ) {
                MapFilterChip(
                    text = "타임 세일",
                    iconResource = null,
                    selected =
                        timeSaleSelected,
                    enabled = true,
                    onClick =
                        onTimeSaleClick,
                )

                MapFilterChip(
                    text = "핫딜",
                    iconResource =
                        R.drawable.ic_hot_deal,
                    selected =
                        hotPlaceSelected,
                    enabled = true,
                    onClick =
                        onHotPlaceClick,
                )

                MapFilterChip(
                    text = "예약 가능",
                    iconResource =
                        R.drawable.ic_reservation,
                    selected = false,
                    enabled = false,
                    onClick = {},
                )
            }
        }
    }
}

@Composable
private fun MapSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
) {
    Surface(
        shape =
            RoundedCornerShape(28.dp),
        color =
            MaterialTheme
                .colorScheme
                .surface,
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    MaterialTheme
                        .colorScheme
                        .primary
                        .copy(alpha = 0.35f),
            ),
        shadowElevation = 2.dp,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp),
    ) {
        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                ),
        ) {
            Icon(
                painter =
                    painterResource(
                        R.drawable.ic_search
                    ),
                contentDescription = "검색",
                tint =
                    MaterialTheme
                        .colorScheme
                        .primary,
                modifier =
                    Modifier.size(25.dp),
            )

            Spacer(
                modifier =
                    Modifier.width(12.dp),
            )

            BasicTextField(
                value = query,
                onValueChange =
                    onQueryChange,
                singleLine = true,
                textStyle =
                    TextStyle(
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,
                        fontSize = 17.sp,
                    ),
                keyboardOptions =
                    KeyboardOptions(
                        imeAction =
                            ImeAction.Search,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onSearch = {
                            onSearch()
                        },
                    ),
                decorationBox = {
                    if (query.isBlank()) {
                        Text(
                            text =
                                "지역 혜택 검색...",
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurface
                                    .copy(
                                        alpha = 0.5f
                                    ),
                            fontSize = 17.sp,
                        )
                    }

                    it()
                },
                modifier =
                    Modifier.weight(1f),
            )

            Icon(
                painter =
                    painterResource(
                        R.drawable
                            .ic_my_location
                    ),
                contentDescription =
                    "내 위치에서 새로고침",
                tint =
                    MaterialTheme
                        .colorScheme
                        .primary,
                modifier =
                    Modifier
                        .size(27.dp)
                        .clickable {
                            onRefresh()
                        },
            )
        }
    }
}

@Composable
private fun MapFilterChip(
    text: String,
    @DrawableRes
    iconResource: Int?,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val primary =
        MaterialTheme
            .colorScheme
            .primary

    val contentColor =
        when {
            selected ->
                MaterialTheme
                    .colorScheme
                    .onPrimary

            enabled ->
                MaterialTheme
                    .colorScheme
                    .onSurface

            else ->
                MaterialTheme
                    .colorScheme
                    .onSurface
                    .copy(alpha = 0.45f)
        }

    Surface(
        onClick = onClick,
        enabled = enabled,
        color =
            if (selected) {
                primary
            } else {
                MaterialTheme
                    .colorScheme
                    .surface
            },
        contentColor = contentColor,
        shape =
            RoundedCornerShape(24.dp),
        border =
            if (selected) {
                null
            } else {
                BorderStroke(
                    width = 1.dp,
                    color =
                        primary.copy(
                            alpha = 0.35f
                        ),
                )
            },
        shadowElevation = 2.dp,
    ) {
        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.Center,
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 11.dp,
                ),
        ) {
            if (iconResource != null) {
                Icon(
                    painter =
                        painterResource(
                            iconResource
                        ),
                    contentDescription = null,
                    tint =
                        when {
                            selected ->
                                MaterialTheme
                                    .colorScheme
                                    .onPrimary

                            enabled ->
                                primary

                            else ->
                                contentColor
                        },
                    modifier =
                        Modifier.size(18.dp),
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp),
                )
            }

            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight =
                    if (selected) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Medium
                    },
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
        shape =
            RoundedCornerShape(14.dp),
        color =
            MaterialTheme
                .colorScheme
                .surface,
        shadowElevation = 7.dp,
        modifier =
            modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            modifier =
                Modifier.padding(14.dp),
        ) {
            Surface(
                color =
                    MaterialTheme
                        .colorScheme
                        .primaryContainer,
                shape =
                    RoundedCornerShape(10.dp),
                modifier =
                    Modifier.size(72.dp),
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center,
                ) {
                    Icon(
                        painter =
                            painterResource(
                                markerForCard(
                                    store.category
                                )
                            ),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier =
                            Modifier.size(42.dp),
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.width(14.dp),
            )

            Column(
                modifier =
                    Modifier.weight(1f),
            ) {
                Text(
                    text = store.name,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurface,
                    fontSize = 19.sp,
                    fontWeight =
                        FontWeight.Bold,
                    maxLines = 1,
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp),
                )

                Text(
                    text =
                        store.address
                            .ifBlank {
                                store.category
                            },
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurface
                            .copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    maxLines = 1,
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp),
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(7.dp),
                ) {
                    if (
                        store.crowdLevel != null
                    ) {
                        StoreBadge(
                            text =
                                crowdLabel(
                                    store.crowdLevel
                                ),
                        )
                    }

                    if (store.timeSaleActive) {
                        StoreBadge(
                            text = "타임세일",
                        )
                    }

                    if (store.externalData) {
                        StoreBadge(
                            text = "공공데이터",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreBadge(
    text: String,
) {
    Surface(
        color =
            MaterialTheme
                .colorScheme
                .primaryContainer,
        shape =
            RoundedCornerShape(6.dp),
    ) {
        Text(
            text = text,
            color =
                MaterialTheme
                    .colorScheme
                    .primary,
            fontSize = 11.sp,
            modifier =
                Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 4.dp,
                ),
        )
    }
}

@Composable
private fun MapBottomNavigation(
    modifier: Modifier = Modifier,
) {
    Surface(
        color =
            MaterialTheme
                .colorScheme
                .background,
        shadowElevation = 10.dp,
        modifier =
            modifier
                .fillMaxWidth()
                .height(88.dp),
    ) {
        Row(
            horizontalArrangement =
                Arrangement.SpaceEvenly,
            verticalAlignment =
                Alignment.CenterVertically,
            modifier =
                Modifier.fillMaxSize(),
        ) {
            MapBottomItem(
                symbol = "◇",
                text = "지도",
                selected = true,
            )

            MapBottomItem(
                symbol = "▤",
                text = "목록",
                selected = false,
            )

            MapBottomItem(
                symbol = "◖",
                text = "프로모션",
                selected = false,
                showDot = true,
            )

            MapBottomItem(
                symbol = "♙",
                text = "마이페이지",
                selected = false,
            )
        }
    }
}

@Composable
private fun MapBottomItem(
    symbol: String,
    text: String,
    selected: Boolean,
    showDot: Boolean = false,
) {
    val primary =
        MaterialTheme
            .colorScheme
            .primary

    Surface(
        color =
            if (selected) {
                primary.copy(alpha = 0.18f)
            } else {
                Color.Transparent
            },
        shape =
            RoundedCornerShape(14.dp),
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 8.dp,
                ),
        ) {
            Box {
                Text(
                    text = symbol,
                    color =
                        if (selected) {
                            primary
                        } else {
                            MaterialTheme
                                .colorScheme
                                .onSurface
                                .copy(alpha = 0.65f)
                        },
                    fontSize = 25.sp,
                )

                if (showDot) {
                    Box(
                        modifier =
                            Modifier
                                .align(
                                    Alignment.TopEnd
                                )
                                .size(7.dp)
                                .background(
                                    color = primary,
                                    shape = CircleShape,
                                ),
                    )
                }
            }

            Text(
                text = text,
                color =
                    if (selected) {
                        primary
                    } else {
                        MaterialTheme
                            .colorScheme
                            .onSurface
                            .copy(alpha = 0.65f)
                    },
                fontSize = 12.sp,
            )
        }
    }
}

@DrawableRes
private fun markerForCard(
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
    level: String?,
): String {
    return when (
        level?.uppercase()
    ) {
        "RELAXED" -> "쾌적함"
        "NORMAL" -> "보통"
        "BUSY" -> "혼잡"
        "VERY_BUSY" -> "매우 혼잡"
        else -> "상태 확인"
    }
}