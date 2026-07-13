package com.example.oshu_android.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onboardingPreferences:
    OnboardingPreferences,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = onboardingPages
    val pagerState = rememberPagerState(
        pageCount = {
            pages.size
        },
    )
    val coroutineScope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    var isCompleting by rememberSaveable {
        mutableStateOf(false)
    }

    fun completeOnboarding() {
        if (isCompleting) {
            return
        }

        isCompleting = true

        coroutineScope.launch {
            try {
                onboardingPreferences.setCompleted()
            } finally {
                onFinished()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                horizontal = 20.dp,
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            Text(
                text = "OSHU",
                color = colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "건너뛰기",
                color = colorScheme.onBackground,
                fontSize = 14.sp,
                modifier = Modifier.clickable(
                    enabled = !isCompleting,
                    onClick = {
                        completeOnboarding()
                    },
                ),
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            pageSpacing = 20.dp,
        ) { pageIndex ->
            val page = pages[pageIndex]

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
            ) {
                Spacer(
                    modifier = Modifier.weight(0.22f)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1f)
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .background(colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = colorScheme.outline
                                .copy(alpha = 0.35f),
                            shape =
                                RoundedCornerShape(20.dp),
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(
                            page.imageRes
                        ),
                        contentDescription =
                            page.imageDescription,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }

                Spacer(
                    modifier = Modifier.weight(0.28f)
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
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = page.description,
                    color =
                        colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp,
                )

                Spacer(
                    modifier = Modifier.weight(0.22f)
                )
            }
        }

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(8.dp),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            pages.indices.forEach { index ->
                val isSelected =
                    pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(
                            if (isSelected) {
                                26.dp
                            } else {
                                8.dp
                            }
                        )
                        .clip(
                            if (isSelected) {
                                RoundedCornerShape(50)
                            } else {
                                CircleShape
                            }
                        )
                        .background(
                            if (isSelected) {
                                colorScheme.primary
                            } else {
                                colorScheme.outline
                                    .copy(alpha = 0.45f)
                            }
                        ),
                )
            }
        }

        Spacer(
            modifier = Modifier.height(54.dp)
        )

        Button(
            onClick = {
                if (
                    pagerState.currentPage ==
                    pages.lastIndex
                ) {
                    completeOnboarding()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1
                        )
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
                disabledContainerColor =
                    colorScheme.primary.copy(
                        alpha = 0.55f
                    ),
            ),
        ) {
            Text(
                text =
                    if (
                        pagerState.currentPage ==
                        pages.lastIndex
                    ) {
                        "시작하기"
                    } else {
                        "다음"
                    },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}