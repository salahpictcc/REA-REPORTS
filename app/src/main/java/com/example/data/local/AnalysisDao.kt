package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {

    @Query("SELECT * FROM analyses ORDER BY createdAt DESC")
    fun getAllAnalyses(): Flow<List<AnalysisEntity>>

    @Query("SELECT COUNT(*) FROM analyses")
    fun getTotalAnalysesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analyses WHERE status = 'COMPLETED'")
    fun getCompletedJobsCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(criticalFindings), 0) FROM analyses")
    fun getTotalCriticalFindings(): Flow<Int>

    @Query("SELECT COALESCE(SUM(apiReferencesCount), 0) FROM analyses")
    fun getTotalApiReferences(): Flow<Int>

    @Query("SELECT * FROM analyses WHERE id = :id LIMIT 1")
    fun getAnalysisById(id: Long): Flow<AnalysisEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: AnalysisEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(analyses: List<AnalysisEntity>)

    @Update
    suspend fun updateAnalysis(analysis: AnalysisEntity)

    @Delete
    suspend fun deleteAnalysis(analysis: AnalysisEntity)

    @Query("DELETE FROM analyses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM analyses")
    suspend fun clearAll()
}
