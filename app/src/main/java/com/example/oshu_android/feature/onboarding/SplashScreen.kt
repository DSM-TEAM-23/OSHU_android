package com.example.oshu_android.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.oshu_android.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onboardingPreferences: OnboardingPreferences,
    onOnboardingRequired: () -> Unit,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompleted by onboardingPreferences.isCompleted.collectAsState(
        initial = null,
    )

    LaunchedEffect(isCompleted) {
        val completed = isCompleted ?: return@LaunchedEffect
        delay(1200)

        if (completed) {
            onLoginRequired()
        } else {
            onOnboardingRequired()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(
                R.drawable.img_logo_oshu,
            ),
            contentDescription = "OSHU",
            modifier = Modifier
                .width(380.dp)
                .height(180.dp)
                .graphicsLayer(
                    scaleX = 2.2f,
                    scaleY = 2.2f,
                ),
            contentScale = ContentScale.Fit,
        )
    }
}
