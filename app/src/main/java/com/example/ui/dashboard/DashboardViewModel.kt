package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AnalysisEntity
import com.example.data.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AnalysisFilter(val label: String) {
    ALL("All Jobs"),
    COMPLETED("Completed"),
    IN_PROGRESS("In Progress"),
    WITH_CRITICAL("Critical Alerts")
}

data class DashboardMetrics(
    val totalAnalyses: Int = 0,
    val completedJobs: Int = 0,
    val criticalFindings: Int = 0,
    val apiReferences: Int = 0
)

private data class DashboardControlsState(
    val searchQuery: String = "",
    val activeFilter: AnalysisFilter = AnalysisFilter.ALL,
    val selectedAnalysis: AnalysisEntity? = null,
    val isAddDialogOpen: Boolean = false
)

data class DashboardUiState(
    val totalAnalyses: Int = 0,
    val completedJobs: Int = 0,
    val criticalFindings: Int = 0,
    val apiReferences: Int = 0,
    val analyses: List<AnalysisEntity> = emptyList(),
    val filteredAnalyses: List<AnalysisEntity> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: AnalysisFilter = AnalysisFilter.ALL,
    val selectedAnalysis: AnalysisEntity? = null,
    val isAddDialogOpen: Boolean = false,
    val isLoading: Boolean = false
)

class DashboardViewModel(private val repository: AnalysisRepository) : ViewModel() {

    private val _controls = MutableStateFlow(DashboardControlsState())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    private val metricsFlow: Flow<DashboardMetrics> = combine(
        repository.totalAnalysesCount,
        repository.completedJobsCount,
        repository.totalCriticalFindings,
        repository.totalApiReferences
    ) { total, completed, critical, apis ->
        DashboardMetrics(
            totalAnalyses = total,
            completedJobs = completed,
            criticalFindings = critical,
            apiReferences = apis
        )
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        metricsFlow,
        repository.allAnalyses,
        _controls
    ) { metrics, list, controls ->
        val filtered = list.filter { item ->
            val matchesQuery = controls.searchQuery.isBlank() ||
                item.fileName.contains(controls.searchQuery, ignoreCase = true) ||
                item.packageName.contains(controls.searchQuery, ignoreCase = true) ||
                item.appName.contains(controls.searchQuery, ignoreCase = true)

            val matchesFilter = when (controls.activeFilter) {
                AnalysisFilter.ALL -> true
                AnalysisFilter.COMPLETED -> item.status.equals("COMPLETED", ignoreCase = true)
                AnalysisFilter.IN_PROGRESS -> item.status.equals("IN_PROGRESS", ignoreCase = true)
                AnalysisFilter.WITH_CRITICAL -> item.criticalFindings > 0
            }

            matchesQuery && matchesFilter
        }

        DashboardUiState(
            totalAnalyses = metrics.totalAnalyses,
            completedJobs = metrics.completedJobs,
            criticalFindings = metrics.criticalFindings,
            apiReferences = metrics.apiReferences,
            analyses = list,
            filteredAnalyses = filtered,
            searchQuery = controls.searchQuery,
            activeFilter = controls.activeFilter,
            selectedAnalysis = controls.selectedAnalysis,
            isAddDialogOpen = controls.isAddDialogOpen,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _controls.update { it.copy(searchQuery = query) }
    }

    fun onFilterSelected(filter: AnalysisFilter) {
        _controls.update { it.copy(activeFilter = filter) }
    }

    fun onSelectAnalysis(analysis: AnalysisEntity?) {
        _controls.update { it.copy(selectedAnalysis = analysis) }
    }

    fun setAddDialogOpen(open: Boolean) {
        _controls.update { it.copy(isAddDialogOpen = open) }
    }

    fun addNewAnalysis(
        fileName: String,
        packageName: String,
        appName: String,
        criticalFindings: Int,
        highFindings: Int,
        apiReferencesCount: Int,
        status: String
    ) {
        viewModelScope.launch {
            val entity = AnalysisEntity(
                fileName = fileName.ifBlank { "app-debug.apk" },
                packageName = packageName.ifBlank { "com.example.unnamed" },
                appName = appName.ifBlank { "Audited App" },
                versionName = "1.0.0",
                fileSizeBytes = 18_500_000,
                sha256 = "b7a9c824" + System.currentTimeMillis().toString(16),
                status = status,
                criticalFindings = criticalFindings,
                highFindings = highFindings,
                mediumFindings = 3,
                lowFindings = 4,
                apiReferencesCount = apiReferencesCount,
                permissionsCount = 10,
                createdAt = System.currentTimeMillis()
            )
            repository.insert(entity)
            _controls.update { it.copy(isAddDialogOpen = false) }
        }
    }

    fun deleteAnalysis(analysis: AnalysisEntity) {
        viewModelScope.launch {
            if (_controls.value.selectedAnalysis?.id == analysis.id) {
                _controls.update { it.copy(selectedAnalysis = null) }
            }
            repository.delete(analysis)
        }
    }

    fun resetToDemoData() {
        viewModelScope.launch {
            repository.clearAll()
            repository.seedInitialDataIfEmpty()
        }
    }
}

class DashboardViewModelFactory(private val repository: AnalysisRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
