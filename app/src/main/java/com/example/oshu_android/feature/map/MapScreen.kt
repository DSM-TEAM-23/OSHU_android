package com.example.oshu_android.feature.map

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oshu_android.R
import com.example.oshu_android.data.store.StoreCardResponse
import com.example.oshu_android.feature.common.MainBottomNavigation
import com.example.oshu_android.feature.common.MainDestination
import kotlinx.coroutines.delay

private val MapBackground = Color(0xFFFFF8F9)
private val MapPrimary = Color(0xFFFF8A9C)
private val MapPrimaryLight = Color(0xFFFFE9ED)
private val MapBrown = Color(0xFF704B50)
private val MapBorder = Color(0xFFFFD6DE)
private val MapHint = Color(0xFF969198)

@Composable
fun MapRoute(
    viewModel: MapViewModel,
    onListClick: () -> Unit = {},
    onPromotionClick: () -> Unit = {},
    onStoreDetailClick: (Long) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    MapScreen(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onTimeSaleClick = viewModel::onTimeSaleClick,
        onHotDealClick = viewModel::onHotDealClick,
        onStoreClick = viewModel::onStoreClick,
        onMapClick = viewModel::onMapClick,
        onMapError = viewModel::onMapError,
        onRefresh = viewModel::refresh,
        onErrorMessageShown = viewModel::onErrorMessageShown,
        onListClick = onListClick,
        onPromotionClick = onPromotionClick,
        onStoreDetailClick = onStoreDetailClick,
    )
}

@Composable
fun MapScreen(
    uiState: MapUiState,
    onSearchQueryChanged: (String) -> Unit,
    onTimeSaleClick: () -> Unit,
    onHotDealClick: () -> Unit,
    onStoreClick: (Long) -> Unit,
    onMapClick: () -> Unit,
    onMapError: (String) -> Unit,
    onRefresh: () -> Unit,
    onErrorMessageShown: () -> Unit,
    onListClick: () -> Unit,
    onPromotionClick: () -> Unit,
    onStoreDetailClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MapBackground),
    ) {
        KakaoMapView(
            stores = uiState.filteredStores,
            selectedStoreId = uiState.selectedStoreId,
            onStoreClick = onStoreClick,
            onMapClick = onMapClick,
            onMapError = onMapError,
            modifier = Modifier.fillMaxSize(),
        )

        MapHeader(
            query = uiState.searchQuery,
            onQueryChanged = onSearchQueryChanged,
            isTimeSaleSelected = uiState.isTimeSaleSelected,
            isHotDealSelected = uiState.isHotDealSelected,
            onTimeSaleClick = onTimeSaleClick,
            onHotDealClick = onHotDealClick,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        uiState.selectedStore?.let { store ->
            SelectedStoreCard(
                store = store,
                onClick = {
                    onStoreDetailClick(store.storeId)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 108.dp,
                    ),
            )
        }

        uiState.errorMessage?.let { message ->
            MapErrorMessage(
                message = message,
                onRefresh = onRefresh,
                onShown = onErrorMessageShown,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 224.dp,
                    ),
            )
        }

        MainBottomNavigation(
            selectedDestination = MainDestination.MAP,
            onDestinationSelected = { destination ->
                when (destination) {
                    MainDestination.MAP -> Unit
                    MainDestination.STORE_LIST -> onListClick()
                    MainDestination.PROMOTION -> onPromotionClick()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun MapHeader(
    query: String,
    onQueryChanged: (String) -> Unit,
    isTimeSaleSelected: Boolean,
    isHotDealSelected: Boolean,
    onTimeSaleClick: () -> Unit,
    onHotDealClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(
        bottomStart = 30.dp,
        bottomEnd = 30.dp,
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = shape,
                clip = false,
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF8FA).copy(alpha = 0.98f),
                        Color(0xFFFFF9FA).copy(alpha = 0.94f),
                        Color(0xFFFFF5F7).copy(alpha = 0.90f),
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 22.dp,
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "OSHU",
                    color = MapPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Spacer(
                modifier = Modifier.height(14.dp),
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
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MapFilterChip(
                    text = "타임 세일",
                    selected = isTimeSaleSelected,
                    onClick = onTimeSaleClick,
                )

                MapFilterChip(
                    text = "핫딜",
                    icon = R.drawable.ic_hot_deal,
                    selected = isHotDealSelected,
                    onClick = onHotDealClick,
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
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .border(
                width = 1.dp,
                color = if (isFocused) MapPrimary else MapBorder,
                shape = RoundedCornerShape(29.dp),
            ),
        shape = RoundedCornerShape(29.dp),
        color = Color.White.copy(alpha = 0.97f),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "검색",
                tint = MapPrimary,
                modifier = Modifier
                    .size(27.dp)
                    .clickable {
                        focusRequester.requestFocus()
                    },
            )

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .padding(horizontal = 16.dp),
                singleLine = true,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(MapPrimary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                    },
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
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
                painter = painterResource(R.drawable.ic_my_location),
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
        color = if (selected) MapPrimary else Color.White.copy(alpha = 0.98f),
        border = if (selected) null else BorderStroke(1.dp, MapBorder),
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            icon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null,
                    tint = when {
                        selected -> Color.White
                        it == R.drawable.ic_hot_deal -> MapPrimary
                        else -> MapBrown
                    },
                    modifier = Modifier.size(20.dp),
                )

                Spacer(
                    modifier = Modifier.width(6.dp),
                )
            }

            Text(
                text = text,
                color = if (selected) Color.White else MapBrown,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val subtitle = store.address.ifBlank {
        store.category.ifBlank {
            "매장 상세 보기"
        }
    }

    val crowdText = store.crowdLevel
        ?.takeIf { it.isNotBlank() }
        ?.let(::crowdLabel)
        ?: "쾌적함"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 10.dp,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BakeryThumbnail()

            Spacer(
                modifier = Modifier.width(14.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = store.name,
                    color = MapBrown,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(
                    modifier = Modifier.height(4.dp),
                )

                Text(
                    text = subtitle,
                    color = MapHint,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(
                    modifier = Modifier.height(9.dp),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    StoreTag(
                        text = crowdText,
                    )

                    StoreTag(
                        text = "포장 가능",
                    )
                }
            }
        }
    }
}

@Composable
private fun BakeryThumbnail() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFE4D6)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFD79563)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_hot_deal),
                contentDescription = "매장 이미지",
                tint = Color.White,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
private fun StoreTag(
    text: String,
) {
    Surface(
        color = MapPrimaryLight,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp,
            ),
            color = MapPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
        )
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
