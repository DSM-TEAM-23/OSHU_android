package com.example.oshu_android.feature.common

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oshu_android.R

enum class MainDestination {
    MAP,
    STORE_LIST,
    PROMOTION,
}

private val NavigationPrimary = Color(0xFFFF8A9C)
private val NavigationBrown = Color(0xFF704B50)

@Composable
fun MainBottomNavigation(
    selectedDestination: MainDestination,
    onDestinationSelected: (MainDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp),
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MainNavigationItem(
                label = "지도",
                selected = selectedDestination == MainDestination.MAP,
                selectedIcon = R.drawable.ic_map_fill,
                unselectedIcon = R.drawable.ic_map,
                onClick = {
                    onDestinationSelected(MainDestination.MAP)
                },
            )

            MainNavigationItem(
                label = "목록",
                selected = selectedDestination == MainDestination.STORE_LIST,
                selectedIcon = R.drawable.ic_inventory_fill,
                unselectedIcon = R.drawable.ic_inventory,
                onClick = {
                    onDestinationSelected(MainDestination.STORE_LIST)
                },
            )

            MainNavigationItem(
                label = "프로모션",
                selected = selectedDestination == MainDestination.PROMOTION,
                selectedIcon = R.drawable.ic_promotion_fill,
                unselectedIcon = R.drawable.ic_promotion,
                onClick = {
                    onDestinationSelected(MainDestination.PROMOTION)
                },
                showBadge = true,
            )
        }
    }
}

@Composable
private fun MainNavigationItem(
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
            .height(70.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) {
                    NavigationPrimary
                } else {
                    Color.Transparent
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
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
                    tint = if (selected) Color.White else NavigationBrown,
                    modifier = Modifier.size(26.dp),
                )

                if (showBadge && !selected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(8.dp)
                            .background(
                                color = NavigationPrimary,
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
                color = if (selected) Color.White else NavigationBrown,
                fontSize = 12.sp,
            )
        }
    }
}