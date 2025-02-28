package com.example.dealhunter

import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.example.dealhunter.utils.NetworkUtils
import java.io.InputStream

class DealHunterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupGlide()
    }

    private fun setupGlide() {
        // Use the same unsafe client for Glide
        val okHttpClient = NetworkUtils.createUnsafeOkHttpClient()

        Glide.get(this).registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttpClient)
        )
    }
}
