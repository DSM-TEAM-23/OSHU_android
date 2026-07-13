package com.example.oshu_android.feature.onboarding

import androidx.annotation.DrawableRes
import com.example.oshu_android.R

data class OnboardingPage(
    @DrawableRes
    val imageRes: Int,
    val imageDescription: String,
    val title: String,
    val description: String,
)

val onboardingPages = listOf(
    OnboardingPage(
        imageRes =
            R.drawable.img_onboarding_discount,
        imageDescription =
            "실시간 동네 할인 소개",
        title =
            "실시간 동네 할인",
        description =
            "유성구 단골 가게의 반짝 할인을\n지금 바로 확인하고 혜택을 누리세요!",
    ),
    OnboardingPage(
        imageRes =
            R.drawable.img_onboarding_table,
        imageDescription =
            "가게 테이블 자리 확인 소개",
        title =
            "헛걸음 없는 테이블 자리",
        description =
            "출발하기 전 빈자리 확인,\n웨이팅 없이 완벽한 타이밍!",
    ),
    OnboardingPage(
        imageRes =
            R.drawable.img_onboarding_timesale,
        imageDescription =
            "타임세일과 실시간 행사 소개",
        title =
            "놓치면 끝나는 타임세일\n& 실시간 행사",
        description =
            "퇴근길, 점심시간!\n핫타임에만 만나는 특별한 할인",
    ),
)