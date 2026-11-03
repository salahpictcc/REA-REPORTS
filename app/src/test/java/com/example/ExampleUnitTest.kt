package com.example

import com.example.data.local.AnalysisEntity
import com.example.ui.dashboard.AnalysisFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `test analysis entity total findings computation`() {
        val analysis = AnalysisEntity(
            fileName = "secure-wallet.apk",
            packageName = "com.wallet.secure",
            appName = "Secure Wallet",
            criticalFindings = 3,
            highFindings = 4,
            mediumFindings = 5,
            lowFindings = 6,
            apiReferencesCount = 42
        )

        assertEquals(18, analysis.totalFindings)
        assertEquals(3, analysis.criticalFindings)
        assertEquals(42, analysis.apiReferencesCount)
    }

    @Test
    fun `test filter predicate logic for completed and critical jobs`() {
        val jobs = listOf(
            AnalysisEntity(
                id = 1,
                fileName = "app-a.apk",
                packageName = "com.test.a",
                appName = "App A",
                status = "COMPLETED",
                criticalFindings = 2,
                apiReferencesCount = 10
            ),
            AnalysisEntity(
                id = 2,
                fileName = "app-b.apk",
                packageName = "com.test.b",
                appName = "App B",
                status = "IN_PROGRESS",
                criticalFindings = 0,
                apiReferencesCount = 5
            ),
            AnalysisEntity(
                id = 3,
                fileName = "app-c.apk",
                packageName = "com.test.c",
                appName = "App C",
                status = "COMPLETED",
                criticalFindings = 0,
                apiReferencesCount = 20
            )
        )

        // Summary calculations
        val totalAnalyses = jobs.size
        val completedJobs = jobs.count { it.status.equals("COMPLETED", ignoreCase = true) }
        val criticalFindings = jobs.sumOf { it.criticalFindings }
        val apiReferences = jobs.sumOf { it.apiReferencesCount }

        assertEquals(3, totalAnalyses)
        assertEquals(2, completedJobs)
        assertEquals(2, criticalFindings)
        assertEquals(35, apiReferences)

        // Filter: WITH_CRITICAL
        val criticalJobs = jobs.filter { it.criticalFindings > 0 }
        assertEquals(1, criticalJobs.size)
        assertEquals("app-a.apk", criticalJobs[0].fileName)

        // Filter: COMPLETED
        val completedList = jobs.filter { it.status.equals("COMPLETED", ignoreCase = true) }
        assertEquals(2, completedList.size)
    }
}
