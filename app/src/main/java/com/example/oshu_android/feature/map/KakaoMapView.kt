package com.example.oshu_android.feature.map

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.oshu_android.R
import com.example.oshu_android.data.store.StoreCardResponse
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.LabelOptions
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun KakaoMapView(
    stores: List<StoreCardResponse>,
    selectedStoreId: Long?,
    onStoreClick: (Long) -> Unit,
    onMapClick: () -> Unit,
    onMapError: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialLatitude: Double =
        MapViewModel.INITIAL_LATITUDE,
    initialLongitude: Double =
        MapViewModel.INITIAL_LONGITUDE,
    initialZoomLevel: Int = 14,
) {
    val context = LocalContext.current

    val lifecycleOwner =
        LocalLifecycleOwner.current

    val currentOnStoreClick by
    rememberUpdatedState(
        newValue = onStoreClick,
    )

    val currentOnMapClick by
    rememberUpdatedState(
        newValue = onMapClick,
    )

    val currentOnMapError by
    rememberUpdatedState(
        newValue = onMapError,
    )

    val mapView =
        remember(context) {
            MapView(context)
        }

    val hasStarted =
        remember {
            AtomicBoolean(false)
        }

    var kakaoMap by
    remember {
        mutableStateOf<KakaoMap?>(null)
    }

    AndroidView(
        factory = {
            mapView.apply {
                if (
                    hasStarted.compareAndSet(
                        false,
                        true,
                    )
                ) {
                    start(
                        object :
                            MapLifeCycleCallback() {

                            override fun onMapDestroy() {
                                kakaoMap = null
                            }

                            override fun onMapError(
                                error: Exception,
                            ) {
                                currentOnMapError(
                                    error.message
                                        ?: "지도를 불러오지 못했습니다."
                                )
                            }
                        },
                        object :
                            KakaoMapReadyCallback() {

                            override fun onMapReady(
                                map: KakaoMap,
                            ) {
                                kakaoMap = map

                                map.setOnLabelClickListener {
                                        _,
                                        _,
                                        label,
                                    ->
                                    val storeId =
                                        (
                                                label.tag
                                                        as? Number
                                                )
                                            ?.toLong()

                                    if (storeId != null) {
                                        currentOnStoreClick(
                                            storeId
                                        )
                                    }

                                    true
                                }

                                map.setOnMapClickListener {
                                        _,
                                        _,
                                        _,
                                        _
                                    ->
                                    currentOnMapClick()
                                }
                            }

                            override fun getPosition():
                                    LatLng {
                                return LatLng.from(
                                    initialLatitude,
                                    initialLongitude,
                                )
                            }

                            override fun getZoomLevel():
                                    Int {
                                return initialZoomLevel
                            }
                        },
                    )

                    setFinishManually(true)
                }
            }
        },
        modifier = modifier,
    )

    LaunchedEffect(
        kakaoMap,
        stores,
        selectedStoreId,
    ) {
        val map =
            kakaoMap
                ?: return@LaunchedEffect

        val layer =
            map.labelManager
                ?.layer
                ?: return@LaunchedEffect

        layer.removeAll()
        layer.setClickable(true)

        stores.forEach { store ->
            val latitude =
                store.latitude

            val longitude =
                store.longitude

            if (
                latitude == null ||
                longitude == null
            ) {
                return@forEach
            }

            val hasValidLatitude =
                latitude in -90.0..90.0

            val hasValidLongitude =
                longitude in -180.0..180.0

            if (
                !hasValidLatitude ||
                !hasValidLongitude
            ) {
                return@forEach
            }

            val markerResource =
                markerResourceForCategory(
                    category = store.category,
                )

            val rank =
                when {
                    store.storeId ==
                            selectedStoreId ->
                        SELECTED_MARKER_RANK

                    store.timeSaleActive ->
                        TIME_SALE_MARKER_RANK

                    else ->
                        NORMAL_MARKER_RANK
                }

            val position =
                LatLng.from(
                    latitude,
                    longitude,
                )

            val labelOptions =
                LabelOptions.from(
                    "store_${store.storeId}",
                    position,
                )
                    .setStyles(
                        markerResource
                    )
                    .setClickable(true)
                    .setTag(store.storeId)
                    .setRank(rank)

            layer.addLabel(
                labelOptions
            )
        }
    }

    DisposableEffect(
        lifecycleOwner,
        mapView,
    ) {
        val lifecycle =
            lifecycleOwner.lifecycle

        val observer =
            LifecycleEventObserver {
                    _,
                    event,
                ->
                if (
                    !hasStarted.get()
                ) {
                    return@LifecycleEventObserver
                }

                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        runCatching {
                            mapView.resume()
                        }
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        runCatching {
                            mapView.pause()
                        }
                    }

                    else -> Unit
                }
            }

        lifecycle.addObserver(
            observer
        )

        if (
            hasStarted.get() &&
            lifecycle.currentState
                .isAtLeast(
                    Lifecycle.State.RESUMED
                )
        ) {
            runCatching {
                mapView.resume()
            }
        }

        onDispose {
            lifecycle.removeObserver(
                observer
            )

            if (hasStarted.get()) {
                runCatching {
                    mapView.pause()
                }

                runCatching {
                    mapView.finish()
                }
            }
        }
    }
}

@DrawableRes
private fun markerResourceForCategory(
    category: String,
): Int {
    val normalizedCategory =
        category
            .trim()
            .replace(" ", "")
            .lowercase()

    return when (
        normalizedCategory
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

private const val NORMAL_MARKER_RANK =
    0L

private const val TIME_SALE_MARKER_RANK =
    500L

private const val SELECTED_MARKER_RANK =
    1000L