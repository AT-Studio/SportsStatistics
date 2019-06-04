package com.example.alit.sportsstatistics

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

class SportsStatisticsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }
}