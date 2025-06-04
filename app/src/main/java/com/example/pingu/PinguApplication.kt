package com.example.pingu

import android.app.Application
import com.example.common.storage.ServiceLocator

class PinguApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}