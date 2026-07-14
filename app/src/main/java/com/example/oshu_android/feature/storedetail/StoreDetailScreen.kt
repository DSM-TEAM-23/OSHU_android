package com.example.oshu_android.feature.storedetail

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.oshu_android.data.store.toOpeningHoursLabel
import com.example.oshu_android.R
import com.example.oshu_android.data.store.CrowdStatusResponse
import com.example.oshu_android.data.store.StoreDetailResponse
import com.example.oshu_android.data.store.TimeSaleResponse
import com.example.oshu_android.data.store.StoreCardResponse
import com.example.oshu_android.feature.map.KakaoMapView
import com.example.oshu_android.ui.theme.OshuBorder
import com.example.oshu_android.ui.theme.OshuPink
import com.example.oshu_android.ui.theme.OshuPinkLight
import com.example.oshu_android.ui.theme.OshuTextPrimary
import com.example.oshu_android.ui.theme.OshuTextSecondary
import com.example.oshu_android.ui.theme.OshuWhite

private val DetailBackground = Color(0xFFFFF8F9)

@Composable
fun StoreDetailRoute(
    viewModel: StoreDetailViewModel,
    onBackClick: () -> Unit,
    onInquiryClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    StoreDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::refresh,
        onInquiryClick = onInquiryClick,
    )
}

@Composable
fun StoreDetailScreen(
    uiState: StoreDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onInquiryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DetailBackground,
        topBar = {
            StoreDetailTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            val context = LocalContext.current
            StoreDetailBottomBar(
                onInquiryClick = onInquiryClick,
                onDirectionsClick = {
                    uiState.store?.let { store ->
                        val uri = Uri.parse(
                            "geo:${store.latitude ?: 36.3622},${store.longitude ?: 127.3562}?q=${Uri.encode(store.name)}",
                        )
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                },
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = OshuPink)
                }
            }

            uiState.store != null -> {
                StoreDetailContent(
                    store = uiState.store,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            else -> {
                ErrorDetailContent(
                    message = uiState.errorMessage ?: "매장 정보를 불러오지 못했습니다.",
                    onRetry = onRetry,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@Composable
private fun StoreDetailTopBar(onBackClick: () -> Unit) {
    Surface(color = Color(0xFFFFF2F5)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Text("←", color = OshuTextPrimary, fontSize = 28.sp)
            }

            Text(
                text = "OSHU",
                modifier = Modifier.weight(1f),
                color = OshuPink,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            IconButton(onClick = {}) {
                Text("↗", color = OshuTextPrimary, fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun StoreDetailContent(
    store: StoreDetailResponse,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            StoreDetailHero(store = store)
        }

        item {
            StoreDetailInfo(store = store)
        }

        if (store.timeSales.isNotEmpty()) {
            item {
                Text(
                    text = "실시간 타임 세일",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = OshuTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            items(store.timeSales) { timeSale ->
                TimeSaleCard(timeSale = timeSale)
            }
        }

        item {
            LocationInfo(store = store)
        }
    }
}

@Composable
private fun StoreDetailHero(store: StoreDetailResponse) {
    val imageUrl = storeImage(store.category)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = store.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)),
                    ),
                ),
        )
    }
}

@Composable
private fun StoreDetailInfo(store: StoreDetailResponse) {
    val openingHours = store.openingHours.toOpeningHoursLabel()

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = OshuPink,
                shape = RoundedCornerShape(5.dp),
            ) {
                Text(
                    text = store.category,
                    color = OshuWhite,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                )
            }

            if (openingHours != null) {
                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_time),
                        contentDescription = "영업시간",
                        tint = OshuTextSecondary,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = openingHours,
                        color = OshuTextSecondary,
                        fontSize = 11.sp,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = store.name,
            color = OshuTextPrimary,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = store.address,
            color = OshuTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 6.dp),
        )

        store.description?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                color = OshuTextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                modifier = Modifier.padding(top = 14.dp),
            )
        }

        CrowdCard(status = store.crowdStatus)
    }
}

@Composable
private fun CrowdCard(status: CrowdStatusResponse?) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        color = OshuPinkLight,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OshuBorder),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "이용 가능한 테이블", color = OshuTextPrimary, fontSize = 12.sp)
            Text(
                text = status?.label?.ifBlank { "정보 없음" } ?: "정보 없음",
                color = Color(0xFF3B8E45),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun TimeSaleCard(timeSale: TimeSaleResponse) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = Color(0xFFFFF3F4),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OshuBorder),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = storeImage("베이커리"),
                contentDescription = timeSale.productName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(timeSale.productName, color = OshuTextPrimary, fontSize = 14.sp)
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "₩${timeSale.originalPrice}",
                        color = OshuTextSecondary,
                        fontSize = 13.sp,
                        textDecoration = TextDecoration.LineThrough,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "₩${timeSale.salePrice}",
                        color = OshuPink,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Surface(color = OshuPink, shape = RoundedCornerShape(6.dp)) {
                Text(
                    text = "타임 세일",
                    color = OshuWhite,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun LocationInfo(store: StoreDetailResponse) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "위치 정보",
            color = OshuTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, OshuBorder),
        ) {
            KakaoMapView(
                stores = listOf(
                    StoreCardResponse(
                        storeId = store.storeId,
                        name = store.name,
                        category = store.category,
                        latitude = store.latitude,
                        longitude = store.longitude,
                    ),
                ),
                selectedStoreId = store.storeId,
                onStoreClick = {},
                onMapClick = {},
                onMapError = {},
                initialLatitude = store.latitude ?: 36.3622,
                initialLongitude = store.longitude ?: 127.3562,
                initialZoomLevel = 17,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
            )
        }
        Text(
            text = store.address,
            color = OshuTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

@Composable
private fun StoreDetailBottomBar(
    onInquiryClick: () -> Unit,
    onDirectionsClick: () -> Unit,
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onDirectionsClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OshuPink),
                border = androidx.compose.foundation.BorderStroke(1.dp, OshuPink),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_road),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("길찾기", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onInquiryClick,
                modifier = Modifier.weight(1.4f),
                colors = ButtonDefaults.buttonColors(containerColor = OshuPink),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_inquiry),
                    contentDescription = null,
                    tint = OshuWhite,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("문의하기", color = OshuWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ErrorDetailContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = OshuTextSecondary)
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = OshuPink),
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text("다시 시도")
        }
    }
}

private fun storeImage(category: String): String {
    return when (category.trim().replace(" ", "").lowercase()) {
        "카페", "cafe", "coffee" -> "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=1200&q=85"
        "음식점", "식당", "restaurant", "food" -> "https://images.unsplash.com/photo-1515003197210-e0cd71810b5f?w=1200&q=85"
        "마트", "mart" -> "https://images.unsplash.com/photo-1578916171728-46686eac8d58?w=1200&q=85"
        else -> "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=1200&q=85"
    }
}
