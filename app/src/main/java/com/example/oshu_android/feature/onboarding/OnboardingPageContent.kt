package com.example.oshu_android.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(
            modifier = Modifier.weight(0.18f),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = colorScheme.outline.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(page.imageRes),
                contentDescription = page.imageDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(
            modifier = Modifier.weight(0.24f),
        )

        Text(
            text = page.title,
            color = colorScheme.onBackground,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 29.sp,
        )

        Spacer(
            modifier = Modifier.height(14.dp),
        )

        Text(
            text = page.description,
            color = colorScheme.onSurfaceVariant,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp,
        )

        Spacer(
            modifier = Modifier.weight(0.22f),
        )
    }
}
