package com.example.oshu_android

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class OshuApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoMapSdk.init(
            this,
            getString(R.string.kakao_native_app_key)
        )
    }
}