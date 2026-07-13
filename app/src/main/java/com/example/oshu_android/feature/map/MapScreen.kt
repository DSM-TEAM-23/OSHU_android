package com.example.oshu_android.feature.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
) {
    var errorMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        KakaoMapView(
            modifier = Modifier.fillMaxSize(),
            initialLatitude = 36.3624,
            initialLongitude = 127.3445,
            initialZoomLevel = 14,
            onMapReady = {
                errorMessage = null
            },
            onMapError = {
                errorMessage =
                    it.message
                        ?: "지도를 불러오지 못했습니다."
            },
        )

        errorMessage?.let {
            Text(
                text = it,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(20.dp)
                    .background(
                        color =
                            MaterialTheme
                                .colorScheme
                                .errorContainer,
                        shape =
                            RoundedCornerShape(8.dp),
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp,
                    ),
                color =
                    MaterialTheme
                        .colorScheme
                        .onErrorContainer,
                fontSize = 13.sp,
            )
        }
    }
}