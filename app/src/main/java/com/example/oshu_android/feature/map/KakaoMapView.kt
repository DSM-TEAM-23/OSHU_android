package com.example.oshu_android.feature.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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

@Composable
fun KakaoMapView(
    stores: List<StoreCardResponse>,
    selectedStoreId: Long?,
    onStoreClick: (Long) -> Unit,
    onMapClick: () -> Unit,
    onMapError: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialLatitude: Double = MapViewModel.INITIAL_LATITUDE,
    initialLongitude: Double = MapViewModel.INITIAL_LONGITUDE,
    initialZoomLevel: Int = 14,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentOnStoreClick by rememberUpdatedState(onStoreClick)
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val currentOnMapError by rememberUpdatedState(onMapError)

    var kakaoMap by remember {
        mutableStateOf<KakaoMap?>(null)
    }

    var currentLocation by remember {
        mutableStateOf<Location?>(null)
    }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            currentLocation = getLastKnownLocation(context)
        }
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            currentLocation = getLastKnownLocation(context)
        } else {
            locationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    val mapView = remember(context) {
        MapView(context).apply {
            setFinishManually(true)
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.resume()
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.pause()
            mapView.finish()
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() {
                            kakaoMap = null
                        }

                        override fun onMapError(error: Exception) {
                            Log.e("KakaoMap", "지도 초기화 실패", error)

                            currentOnMapError(
                                "지도 오류: ${error.message ?: error.javaClass.simpleName}",
                            )
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun onMapReady(map: KakaoMap) {
                            Log.d("KakaoMap", "지도 준비 완료")
                            kakaoMap = map

                            map.setOnLabelClickListener { _, _, label ->
                                (label.tag as? Number)
                                    ?.toLong()
                                    ?.let(currentOnStoreClick)

                                true
                            }

                            map.setOnMapClickListener { _, _, _, _ ->
                                currentOnMapClick()
                            }
                        }

                        override fun getPosition(): LatLng {
                            return LatLng.from(
                                initialLatitude,
                                initialLongitude,
                            )
                        }

                        override fun getZoomLevel(): Int {
                            return initialZoomLevel
                        }
                    },
                )
            }
        },
        modifier = modifier,
    )

    LaunchedEffect(kakaoMap, stores, selectedStoreId, currentLocation) {
        val map = kakaoMap ?: return@LaunchedEffect
        val layer = map.labelManager?.layer ?: return@LaunchedEffect

        layer.removeAll()
        layer.setClickable(true)

        currentLocation?.let { location ->
            layer.addLabel(
                LabelOptions.from(
                    "current_location",
                    LatLng.from(location.latitude, location.longitude),
                )
                    .setStyles(R.drawable.ic_my_location)
                    .setRank(CURRENT_LOCATION_MARKER_RANK),
            )
        }

        stores.forEach { store ->
            val latitude = store.latitude ?: return@forEach
            val longitude = store.longitude ?: return@forEach

            if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
                return@forEach
            }

            val rank = when {
                store.storeId == selectedStoreId -> SELECTED_MARKER_RANK
                store.timeSaleActive -> TIME_SALE_MARKER_RANK
                else -> NORMAL_MARKER_RANK
            }

            layer.addLabel(
                LabelOptions.from(
                    "store_${store.storeId}",
                    LatLng.from(latitude, longitude),
                )
                    .setStyles(markerResourceForCategory(store.category))
                    .setClickable(true)
                    .setTag(store.storeId)
                    .setRank(rank),
            )
        }
    }
}

private fun getLastKnownLocation(context: Context): Location? {
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

    if (!fineGranted && !coarseGranted) return null

    return manager.getProviders(true)
        .asSequence()
        .mapNotNull { provider ->
            runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
        }
        .maxByOrNull { it.time }
}

@DrawableRes
private fun markerResourceForCategory(category: String): Int {
    return when (category.trim().replace(" ", "").lowercase()) {
        "베이커리", "bakery" -> R.drawable.ic_marker_bakery
        "음식점", "식당", "restaurant", "food" -> R.drawable.ic_marker_food_market
        "카페", "cafe", "coffee" -> R.drawable.ic_marker_cafe
        "마트", "mart" -> R.drawable.ic_marker_mart
        else -> R.drawable.ic_marker_marketplace
    }
}

private const val NORMAL_MARKER_RANK = 0L
private const val TIME_SALE_MARKER_RANK = 500L
private const val SELECTED_MARKER_RANK = 1000L
private const val CURRENT_LOCATION_MARKER_RANK = 2000L
