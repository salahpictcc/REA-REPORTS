package com.example.data.repository

import com.example.data.local.AnalysisDao
import com.example.data.local.AnalysisEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AnalysisRepository(private val analysisDao: AnalysisDao) {

    val allAnalyses: Flow<List<AnalysisEntity>> = analysisDao.getAllAnalyses()
    val totalAnalysesCount: Flow<Int> = analysisDao.getTotalAnalysesCount()
    val completedJobsCount: Flow<Int> = analysisDao.getCompletedJobsCount()
    val totalCriticalFindings: Flow<Int> = analysisDao.getTotalCriticalFindings()
    val totalApiReferences: Flow<Int> = analysisDao.getTotalApiReferences()

    fun getAnalysisById(id: Long): Flow<AnalysisEntity?> = analysisDao.getAnalysisById(id)

    suspend fun insert(analysis: AnalysisEntity): Long = analysisDao.insertAnalysis(analysis)

    suspend fun update(analysis: AnalysisEntity) = analysisDao.updateAnalysis(analysis)

    suspend fun delete(analysis: AnalysisEntity) = analysisDao.deleteAnalysis(analysis)

    suspend fun deleteById(id: Long) = analysisDao.deleteById(id)

    suspend fun clearAll() = analysisDao.clearAll()

    suspend fun seedInitialDataIfEmpty() {
        val count = analysisDao.getTotalAnalysesCount().first()
        if (count == 0) {
            val now = System.currentTimeMillis()
            val sampleData = listOf(
                AnalysisEntity(
                    fileName = "vault-finance-v4.2.apk",
                    packageName = "com.vault.finance.app",
                    appName = "Vault Mobile",
                    versionName = "4.2.1",
                    versionCode = 4210,
                    minSdk = 26,
                    targetSdk = 34,
                    fileSizeBytes = 28_420_000,
                    sha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                    status = "COMPLETED",
                    criticalFindings = 3,
                    highFindings = 7,
                    mediumFindings = 12,
                    lowFindings = 15,
                    apiReferencesCount = 48,
                    permissionsCount = 18,
                    dexCount = 3,
                    createdAt = now - (15 * 60 * 1000)
                ),
                AnalysisEntity(
                    fileName = "iot-smart-mesh-v2.0.apk",
                    packageName = "io.smartmesh.controller",
                    appName = "SmartMesh Home",
                    versionName = "2.0.0",
                    versionCode = 200,
                    minSdk = 24,
                    targetSdk = 33,
                    fileSizeBytes = 14_850_000,
                    sha256 = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4",
                    status = "COMPLETED",
                    criticalFindings = 5,
                    highFindings = 4,
                    mediumFindings = 9,
                    lowFindings = 8,
                    apiReferencesCount = 32,
                    permissionsCount = 12,
                    dexCount = 2,
                    createdAt = now - (2 * 3600 * 1000)
                ),
                AnalysisEntity(
                    fileName = "health-pulse-telecom-v3.1.apk",
                    packageName = "com.healthpulse.telehealth",
                    appName = "HealthPulse",
                    versionName = "3.1.5",
                    versionCode = 315,
                    minSdk = 26,
                    targetSdk = 34,
                    fileSizeBytes = 41_200_000,
                    sha256 = "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb",
                    status = "COMPLETED",
                    criticalFindings = 1,
                    highFindings = 3,
                    mediumFindings = 6,
                    lowFindings = 14,
                    apiReferencesCount = 64,
                    permissionsCount = 16,
                    dexCount = 4,
                    createdAt = now - (6 * 3600 * 1000)
                ),
                AnalysisEntity(
                    fileName = "enterprise-messenger-v1.4.apk",
                    packageName = "org.securechat.enterprise",
                    appName = "SecureChat Work",
                    versionName = "1.4.0",
                    versionCode = 140,
                    minSdk = 28,
                    targetSdk = 34,
                    fileSizeBytes = 19_600_000,
                    sha256 = "1a8565a9da720485a73e445300064f75f327372d3765b472e450dec283ad1364",
                    status = "IN_PROGRESS",
                    criticalFindings = 0,
                    highFindings = 1,
                    mediumFindings = 2,
                    lowFindings = 3,
                    apiReferencesCount = 19,
                    permissionsCount = 9,
                    dexCount = 2,
                    createdAt = now - (10 * 60 * 1000)
                )
            )
            analysisDao.insertAll(sampleData)
        }
    }
}
