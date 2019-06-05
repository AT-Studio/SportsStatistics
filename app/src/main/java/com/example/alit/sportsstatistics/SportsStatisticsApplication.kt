package com.example.alit.sportsstatistics

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.squareup.leakcanary.LeakCanary

class SportsStatisticsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
        Fresco.initialize(this)
    }
}