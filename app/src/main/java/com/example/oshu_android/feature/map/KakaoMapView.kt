package com.example.oshu_android.feature.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.util.Log
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.oshu_android.R
import com.example.oshu_android.data.store.StoreCardResponse
import com.example.oshu_android.data.store.TimeSaleSchedule
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import kotlinx.coroutines.delay

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
    fitCurrentLocation: Boolean = false,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentOnStoreClick by rememberUpdatedState(onStoreClick)
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val currentOnMapError by rememberUpdatedState(onMapError)
    val storeMarkerBitmap = remember(context) {
        requireNotNull(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_marker_pink,
            ),
        ).toBitmap()
    }
    var currentTimeMillis by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }
    val hasTimedDiscount = stores.any { store ->
        store.timeSaleActive &&
            store.discountRate?.let { it > 0 } == true &&
            !store.timeSaleEndAt.isNullOrBlank()
    }

    LaunchedEffect(hasTimedDiscount) {
        if (!hasTimedDiscount) return@LaunchedEffect

        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1_000L)
        }
    }
    val currentLocationMarkerBitmap = remember(context) {
        requireNotNull(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_current_location_marker,
            ),
        ).toBitmap()
    }

    var kakaoMap by remember {
        mutableStateOf<KakaoMap?>(null)
    }

    var currentLocation by remember {
        mutableStateOf<Location?>(null)
    }

    var hasLocationPermission by remember {
        mutableStateOf(false)
    }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            hasLocationPermission = true
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
            hasLocationPermission = true
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

    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            onDispose { }
        } else {
            val locationManager = context.getSystemService(
                Context.LOCATION_SERVICE,
            ) as LocationManager
            val listener = LocationListener { location ->
                currentLocation = location
            }
            val providers = locationManager.getProviders(true)

            providers.forEach { provider ->
                runCatching {
                    locationManager.requestLocationUpdates(
                        provider,
                        2_000L,
                        5f,
                        listener,
                        Looper.getMainLooper(),
                    )
                }
            }

            onDispose {
                runCatching {
                    locationManager.removeUpdates(listener)
                }
            }
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

    LaunchedEffect(
        kakaoMap,
        stores,
        selectedStoreId,
        currentLocation,
        currentTimeMillis,
    ) {
        val map = kakaoMap ?: return@LaunchedEffect
        val layer = map.labelManager?.layer ?: return@LaunchedEffect

        layer.removeAll()
        layer.setClickable(true)

        currentLocation?.let { location ->
            val currentPosition = LatLng.from(location.latitude, location.longitude)
            layer.addLabel(
                LabelOptions.from(
                    "current_location",
                    currentPosition,
                )
                    .setStyles(currentLocationMarkerBitmap)
                    .setRank(CURRENT_LOCATION_MARKER_RANK),
            )

            if (fitCurrentLocation) {
                stores.firstOrNull()?.let { store ->
                    val latitude = store.latitude ?: return@let
                    val longitude = store.longitude ?: return@let
                    val storePosition = LatLng.from(latitude, longitude)

                    if (!map.canShowMapPoints(
                            map.zoomLevel,
                            currentPosition,
                            storePosition,
                        )
                    ) {
                        map.moveCamera(
                            CameraUpdateFactory.fitMapPoints(
                                arrayOf(currentPosition, storePosition),
                                24,
                            ),
                        )
                    }
                }
            }
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
            val remainingTime = if (store.timeSaleActive) {
                TimeSaleSchedule.remainingText(
                    endAt = store.timeSaleEndAt,
                    nowMillis = currentTimeMillis,
                )
            } else {
                null
            }

            layer.addLabel(
                LabelOptions.from(
                    "store_${store.storeId}",
                    LatLng.from(latitude, longitude),
                )
                    .setStyles(
                        LabelStyle.from(
                            store.discountRate
                                ?.takeIf { it > 0 }
                                ?.let {
                                    discountMarkerBitmap(
                                        marker = storeMarkerBitmap,
                                        discountRate = it,
                                        remainingTime = remainingTime,
                                        density = context.resources.displayMetrics.density,
                                    )
                                }
                                ?: storeMarkerBitmap,
                        )
                            .setAnchorPoint(0.5f, 1f),
                    )
                    .setClickable(true)
                    .setTag(store.storeId)
                    .setRank(rank),
            )
        }
    }
}

private fun discountMarkerBitmap(
    marker: Bitmap,
    discountRate: Int,
    remainingTime: String?,
    density: Float,
): Bitmap {
    val horizontalPadding = 8f * density
    val verticalPadding = 3f * density
    val gap = 4f * density
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 12f * density
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    val label = listOfNotNull(
        "${discountRate}% 할인",
        remainingTime?.let { "남은 $it" },
    ).joinToString(" · ")
    val textBounds = textPaint.fontMetrics
    val labelWidth = maxOf(
        marker.width.toFloat(),
        textPaint.measureText(label) + horizontalPadding * 2,
    )
    val labelHeight = textBounds.descent - textBounds.ascent + verticalPadding * 2
    val bitmap = Bitmap.createBitmap(
        labelWidth.toInt(),
        (labelHeight + gap + marker.height).toInt(),
        Bitmap.Config.ARGB_8888,
    )
    val canvas = Canvas(bitmap)
    val markerLeft = (bitmap.width - marker.width) / 2f

    canvas.drawRoundRect(
        RectF(0f, 0f, bitmap.width.toFloat(), labelHeight),
        labelHeight / 2,
        labelHeight / 2,
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(255, 94, 147) },
    )
    canvas.drawText(
        label,
        bitmap.width / 2f,
        verticalPadding - textBounds.ascent,
        textPaint,
    )
    canvas.drawBitmap(
        marker,
        markerLeft,
        labelHeight + gap,
        null,
    )

    return bitmap
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


private const val NORMAL_MARKER_RANK = 0L
private const val TIME_SALE_MARKER_RANK = 500L
private const val SELECTED_MARKER_RANK = 1000L
private const val CURRENT_LOCATION_MARKER_RANK = 2000L
