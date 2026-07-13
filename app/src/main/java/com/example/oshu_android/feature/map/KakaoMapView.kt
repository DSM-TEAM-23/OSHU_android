package com.example.oshu_android.feature.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

@Composable
fun KakaoMapView(
    modifier: Modifier = Modifier,
    initialLatitude: Double = 36.3624,
    initialLongitude: Double = 127.3445,
    initialZoomLevel: Int = 14,
    onMapReady: (KakaoMap) -> Unit = {},
    onMapError: (Exception) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentOnMapReady =
        rememberUpdatedState(onMapReady)

    val currentOnMapError =
        rememberUpdatedState(onMapError)

    val mapView = remember {
        MapView(context)
    }

    AndroidView(
        factory = {
            mapView.apply {
                start(
                    object :
                        MapLifeCycleCallback() {
                        override fun onMapDestroy() {
                        }

                        override fun onMapError(
                            error: Exception,
                        ) {
                            currentOnMapError.value(
                                error
                            )
                        }
                    },
                    object :
                        KakaoMapReadyCallback() {
                        override fun onMapReady(
                            kakaoMap: KakaoMap,
                        ) {
                            currentOnMapReady.value(
                                kakaoMap
                            )
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
        },
        modifier = modifier,
    )

    DisposableEffect(
        lifecycleOwner,
        mapView,
    ) {
        val observer =
            LifecycleEventObserver {
                    _,
                    event,
                ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        mapView.resume()
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        mapView.pause()
                    }

                    else -> {
                    }
                }
            }

        lifecycleOwner.lifecycle.addObserver(
            observer
        )

        if (
            lifecycleOwner.lifecycle.currentState
                .isAtLeast(
                    Lifecycle.State.RESUMED
                )
        ) {
            mapView.resume()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(
                observer
            )

            mapView.pause()
            mapView.finish()
        }
    }
}