package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.AnalysisRepository

class AnalysisApplication : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { AnalysisRepository(database.analysisDao()) }

    override fun onCreate() {
        super.onCreate()
    }
}
