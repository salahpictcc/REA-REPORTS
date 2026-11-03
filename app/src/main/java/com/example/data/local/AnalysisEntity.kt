package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analyses")
data class AnalysisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val packageName: String,
    val appName: String,
    val versionName: String = "1.0.0",
    val versionCode: Long = 1,
    val minSdk: Int = 24,
    val targetSdk: Int = 34,
    val fileSizeBytes: Long = 0,
    val sha256: String = "",
    val status: String = "COMPLETED", // COMPLETED, IN_PROGRESS, FAILED, QUEUED
    val criticalFindings: Int = 0,
    val highFindings: Int = 0,
    val mediumFindings: Int = 0,
    val lowFindings: Int = 0,
    val apiReferencesCount: Int = 0,
    val permissionsCount: Int = 0,
    val dexCount: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalFindings: Int
        get() = criticalFindings + highFindings + mediumFindings + lowFindings
}
