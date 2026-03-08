package com.jht.pointy

import android.app.Application
import com.jht.pointy.data.network.SessionManager

class PointyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.initialize(this)
    }
}
