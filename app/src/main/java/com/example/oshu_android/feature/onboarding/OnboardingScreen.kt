package com.example.oshu_android.feature.onboarding

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.IOException
import kotlinx.coroutines.launch

private const val ONBOARDING_PAGE_DURATION_MILLIS = 5_000

@Composable
fun OnboardingScreen(
    onboardingPreferences: OnboardingPreferences,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = onboardingPages

    if (pages.isEmpty()) {
        return
    }

    var isCompleting by rememberSaveable {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    val pagerState = rememberPagerState(
        pageCount = { pages.size },
    )
    val currentPageIndex = pagerState.settledPage
    val isLastPage = currentPageIndex == pages.lastIndex
    val pageProgress = remember { Animatable(0f) }

    LaunchedEffect(currentPageIndex, isCompleting) {
        pageProgress.snapTo(0f)

        if (!isCompleting) {
            pageProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = ONBOARDING_PAGE_DURATION_MILLIS,
                    easing = LinearEasing,
                ),
            )

            if (!isLastPage && pagerState.settledPage == currentPageIndex) {
                pagerState.animateScrollToPage(currentPageIndex + 1)
            }
        }
    }

    fun completeOnboarding() {
        if (isCompleting) {
            return
        }

        isCompleting = true

        coroutineScope.launch {
            try {
                onboardingPreferences.setCompleted()
                onFinished()
            } catch (_: IOException) {
                isCompleting = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            pages.indices.forEach { index ->
                val segmentProgress = when {
                    index < currentPageIndex -> 1f
                    index == currentPageIndex -> pageProgress.value
                    else -> 0f
                }

                LinearProgressIndicator(
                    progress = { segmentProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = colorScheme.primary,
                    trackColor = colorScheme.outline.copy(alpha = 0.25f),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
        ) {
            Text(
                text = "OSHU",
                modifier = Modifier.align(Alignment.Center),
                color = colorScheme.primary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                text = "${currentPageIndex + 1} / ${pages.size}",
                modifier = Modifier.align(Alignment.CenterStart),
                color = colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
            )

            Text(
                text = "건너뛰기",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(
                        enabled = !isCompleting,
                        onClick = {
                            completeOnboarding()
                        },
                    ),
                color = colorScheme.onBackground,
                fontSize = 14.sp,
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = !isCompleting,
        ) { pageIndex ->
            OnboardingPageContent(
                page = pages[pageIndex],
            )
        }

        Button(
            onClick = {
                if (isLastPage) {
                    completeOnboarding()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(currentPageIndex + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isCompleting,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                disabledContainerColor = colorScheme.primary.copy(alpha = 0.55f),
            ),
        ) {
            Text(
                text = if (isLastPage) "시작하기" else "다음",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp),
        )
    }
}
