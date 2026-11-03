package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AnalysisEntity
import com.example.ui.theme.CriticalBadgeBg
import com.example.ui.theme.CyberCharcoal
import com.example.ui.theme.CyberNavy
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavyElevated
import com.example.ui.theme.CyberSurfaceStroke
import com.example.ui.theme.InfoBadgeBg
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SuccessBadgeBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletBadgeBg
import com.example.ui.theme.WarningBadgeBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        containerColor = CyberNavyDark,
        topBar = {
            DashboardTopBar(
                onResetDemo = { viewModel.resetToDemoData() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setAddDialogOpen(true) },
                containerColor = NeonCyan,
                contentColor = CyberNavyDark,
                modifier = Modifier.testTag("add_analysis_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Analysis Entry"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section: Metric Summary Cards
            item {
                DashboardSummaryGrid(
                    totalAnalyses = uiState.totalAnalyses,
                    completedJobs = uiState.completedJobs,
                    criticalFindings = uiState.criticalFindings,
                    apiReferences = uiState.apiReferences
                )
            }

            // Section: Controls (Search & Filters)
            item {
                DashboardControls(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    activeFilter = uiState.activeFilter,
                    onFilterSelected = viewModel::onFilterSelected
                )
            }

            // Section: Recent Analysis Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AUDIT REGISTRY (${uiState.filteredAnalyses.size})",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = NeonCyan,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "Local Room DB",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            // Section: Analysis Records or Empty State
            if (uiState.filteredAnalyses.isEmpty()) {
                item {
                    EmptyAnalysisState(
                        isFiltered = uiState.searchQuery.isNotBlank() || uiState.activeFilter != AnalysisFilter.ALL,
                        onResetFilters = {
                            viewModel.onSearchQueryChange("")
                            viewModel.onFilterSelected(AnalysisFilter.ALL)
                        },
                        onSeedData = { viewModel.resetToDemoData() }
                    )
                }
            } else {
                items(
                    items = uiState.filteredAnalyses,
                    key = { it.id }
                ) { analysis ->
                    AnalysisCard(
                        analysis = analysis,
                        onClick = { viewModel.onSelectAnalysis(analysis) },
                        onDelete = { viewModel.deleteAnalysis(analysis) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    // Detail Dialog
    uiState.selectedAnalysis?.let { analysis ->
        AnalysisDetailDialog(
            analysis = analysis,
            onDismiss = { viewModel.onSelectAnalysis(null) }
        )
    }

    // Add Analysis Dialog
    if (uiState.isAddDialogOpen) {
        AddAnalysisDialog(
            onDismiss = { viewModel.setAddDialogOpen(false) },
            onConfirm = { fileName, pkgName, appName, critical, high, apis, status ->
                viewModel.addNewAnalysis(fileName, pkgName, appName, critical, high, apis, status)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    onResetDemo: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CyberNavy,
            titleContentColor = TextPrimary
        ),
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REVERSE ENG AI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Text(
                    text = "Security Audit Command Center",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary
                    )
                )
            }
        },
        actions = {
            IconButton(
                onClick = onResetDemo,
                modifier = Modifier.testTag("reset_demo_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Demo Data",
                    tint = NeonCyan
                )
            }
        }
    )
}

@Composable
fun DashboardSummaryGrid(
    totalAnalyses: Int,
    completedJobs: Int,
    criticalFindings: Int,
    apiReferences: Int
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isWide = maxWidth > 560.dp

        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f).testTag("summary_card_total"),
                    title = "Total Analyses",
                    value = totalAnalyses.toString(),
                    badge = "Audit Jobs",
                    icon = Icons.Default.Analytics,
                    accentColor = NeonCyan,
                    badgeBg = InfoBadgeBg
                )
                SummaryCard(
                    modifier = Modifier.weight(1f).testTag("summary_card_completed"),
                    title = "Completed Jobs",
                    value = completedJobs.toString(),
                    badge = if (totalAnalyses > 0) "${(completedJobs * 100 / totalAnalyses.coerceAtLeast(1))}% Done" else "Idle",
                    icon = Icons.Default.CheckCircle,
                    accentColor = NeonGreen,
                    badgeBg = SuccessBadgeBg
                )
                SummaryCard(
                    modifier = Modifier.weight(1f).testTag("summary_card_critical"),
                    title = "Critical Findings",
                    value = criticalFindings.toString(),
                    badge = if (criticalFindings > 0) "Immediate Action" else "Clean",
                    icon = Icons.Default.Warning,
                    accentColor = if (criticalFindings > 0) NeonRed else NeonGreen,
                    badgeBg = if (criticalFindings > 0) CriticalBadgeBg else SuccessBadgeBg
                )
                SummaryCard(
                    modifier = Modifier.weight(1f).testTag("summary_card_apis"),
                    title = "API References",
                    value = apiReferences.toString(),
                    badge = "Endpoints",
                    icon = Icons.Default.Hub,
                    accentColor = NeonViolet,
                    badgeBg = VioletBadgeBg
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f).testTag("summary_card_total"),
                        title = "Total Analyses",
                        value = totalAnalyses.toString(),
                        badge = "Audit Jobs",
                        icon = Icons.Default.Analytics,
                        accentColor = NeonCyan,
                        badgeBg = InfoBadgeBg
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f).testTag("summary_card_completed"),
                        title = "Completed Jobs",
                        value = completedJobs.toString(),
                        badge = if (totalAnalyses > 0) "$completedJobs of $totalAnalyses" else "0%",
                        icon = Icons.Default.CheckCircle,
                        accentColor = NeonGreen,
                        badgeBg = SuccessBadgeBg
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f).testTag("summary_card_critical"),
                        title = "Critical Findings",
                        value = criticalFindings.toString(),
                        badge = if (criticalFindings > 0) "Immediate Action" else "Clean",
                        icon = Icons.Default.Warning,
                        accentColor = if (criticalFindings > 0) NeonRed else NeonGreen,
                        badgeBg = if (criticalFindings > 0) CriticalBadgeBg else SuccessBadgeBg
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f).testTag("summary_card_apis"),
                        title = "API References",
                        value = apiReferences.toString(),
                        badge = "Endpoints",
                        icon = Icons.Default.Hub,
                        accentColor = NeonViolet,
                        badgeBg = VioletBadgeBg
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    badgeBg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, CyberSurfaceStroke, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(
            containerColor = CyberNavyElevated
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardControls(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    activeFilter: AnalysisFilter,
    onFilterSelected: (AnalysisFilter) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_analyses_input"),
            placeholder = {
                Text("Search by APK, package or app name...", color = TextMuted)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextSecondary
                )
            },
            trailingIcon = {
                AnimatedVisibility(visible = searchQuery.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = TextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberNavy,
                unfocusedContainerColor = CyberNavy,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberSurfaceStroke,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnalysisFilter.values().forEach { filter ->
                val selected = filter == activeFilter
                FilterChip(
                    selected = selected,
                    onClick = { onFilterSelected(filter) },
                    label = {
                        Text(
                            text = filter.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CyberNavyElevated,
                        labelColor = TextSecondary,
                        selectedContainerColor = if (filter == AnalysisFilter.WITH_CRITICAL) CriticalBadgeBg else InfoBadgeBg,
                        selectedLabelColor = if (filter == AnalysisFilter.WITH_CRITICAL) NeonRed else NeonCyan
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        borderColor = CyberSurfaceStroke,
                        selectedBorderColor = if (filter == AnalysisFilter.WITH_CRITICAL) NeonRed else NeonCyan,
                        borderWidth = 1.dp
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("filter_chip_${filter.name}")
                )
            }
        }
    }
}

@Composable
fun AnalysisCard(
    analysis: AnalysisEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val formattedTime = remember(analysis.createdAt) { dateFormat.format(Date(analysis.createdAt)) }
    val formattedSize = remember(analysis.fileSizeBytes) {
        String.format(Locale.getDefault(), "%.1f MB", analysis.fileSizeBytes / (1024f * 1024f))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberSurfaceStroke, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("analysis_card_${analysis.id}"),
        colors = CardDefaults.cardColors(
            containerColor = CyberNavy
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: App name and Status pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = analysis.appName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = analysis.packageName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                StatusPill(status = analysis.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata row: APK file name & size
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberCharcoal, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "APK File",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = analysis.fileName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = formattedSize,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Critical, High, APIs, Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (analysis.criticalFindings > 0) {
                        MetricPill(
                            label = "${analysis.criticalFindings} Critical",
                            bgColor = CriticalBadgeBg,
                            textColor = NeonRed
                        )
                    }
                    if (analysis.highFindings > 0) {
                        MetricPill(
                            label = "${analysis.highFindings} High",
                            bgColor = WarningBadgeBg,
                            textColor = NeonAmber
                        )
                    }
                    MetricPill(
                        label = "${analysis.apiReferencesCount} APIs",
                        bgColor = VioletBadgeBg,
                        textColor = NeonViolet
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Analysis",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusPill(status: String) {
    val (bg, textColor, label) = when (status.uppercase(Locale.getDefault())) {
        "COMPLETED" -> Triple(SuccessBadgeBg, NeonGreen, "COMPLETED")
        "IN_PROGRESS" -> Triple(InfoBadgeBg, NeonCyan, "IN PROGRESS")
        "FAILED" -> Triple(CriticalBadgeBg, NeonRed, "FAILED")
        else -> Triple(WarningBadgeBg, NeonAmber, "QUEUED")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun MetricPill(label: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun EmptyAnalysisState(
    isFiltered: Boolean,
    onResetFilters: () -> Unit,
    onSeedData: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberSurfaceStroke, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberNavyElevated),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(InfoBadgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isFiltered) "No matching audits found" else "No APK analyses in registry",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isFiltered)
                    "Try adjusting your search query or switching the active filter tab."
                else
                    "Your local Room database is clean. Seed initial audit data or register a new APK job.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isFiltered) {
                OutlinedButton(
                    onClick = onResetFilters,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                ) {
                    Text("Reset Filters")
                }
            } else {
                Button(
                    onClick = onSeedData,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = CyberNavyDark
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Populate Sample Audits")
                }
            }
        }
    }
}

@Composable
fun AnalysisDetailDialog(
    analysis: AnalysisEntity,
    onDismiss: () -> Unit
) {
    val formattedSize = remember(analysis.fileSizeBytes) {
        String.format(Locale.getDefault(), "%.2f MB", analysis.fileSizeBytes / (1024f * 1024f))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberNavyElevated,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = analysis.appName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DetailRow(label = "Package", value = analysis.packageName)
                DetailRow(label = "File Name", value = analysis.fileName)
                DetailRow(label = "Status", value = analysis.status)
                DetailRow(label = "File Size", value = formattedSize)
                DetailRow(label = "Min / Target SDK", value = "Android ${analysis.minSdk} / ${analysis.targetSdk}")
                DetailRow(label = "DEX Files", value = "${analysis.dexCount} classes.dex")
                DetailRow(label = "Permissions", value = "${analysis.permissionsCount} declared")
                DetailRow(label = "API References", value = "${analysis.apiReferencesCount} endpoints mapped")
                DetailRow(label = "Critical Findings", value = "${analysis.criticalFindings} critical", isDanger = analysis.criticalFindings > 0)
                DetailRow(label = "High Findings", value = "${analysis.highFindings} high", isWarning = analysis.highFindings > 0)

                if (analysis.sha256.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberCharcoal, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "SHA-256 HASH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = analysis.sha256,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeonCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberNavyDark)
            ) {
                Text("Close Inspector")
            }
        }
    )
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    isDanger: Boolean = false,
    isWarning: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = when {
                    isDanger -> NeonRed
                    isWarning -> NeonAmber
                    else -> TextPrimary
                },
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnalysisDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        fileName: String,
        packageName: String,
        appName: String,
        critical: Int,
        high: Int,
        apis: Int,
        status: String
    ) -> Unit
) {
    var fileName by remember { mutableStateOf("sample-security-audit.apk") }
    var packageName by remember { mutableStateOf("com.example.auditedapp") }
    var appName by remember { mutableStateOf("Audit Target") }
    var criticalCount by remember { mutableStateOf("2") }
    var highCount by remember { mutableStateOf("4") }
    var apisCount by remember { mutableStateOf("28") }
    var isCompleted by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberNavyElevated,
        title = {
            Text(
                text = "Register Analysis Entry",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Add an audit job directly into the local Room database to test reactive summary cards.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )

                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text("App Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberSurfaceStroke
                    )
                )

                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberSurfaceStroke
                    )
                )

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("APK File Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberSurfaceStroke
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = criticalCount,
                        onValueChange = { criticalCount = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Critical") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NeonRed,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonRed,
                            unfocusedBorderColor = CyberSurfaceStroke
                        )
                    )
                    OutlinedTextField(
                        value = highCount,
                        onValueChange = { highCount = it.filter { ch -> ch.isDigit() } },
                        label = { Text("High") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NeonAmber,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonAmber,
                            unfocusedBorderColor = CyberSurfaceStroke
                        )
                    )
                    OutlinedTextField(
                        value = apisCount,
                        onValueChange = { apisCount = it.filter { ch -> ch.isDigit() } },
                        label = { Text("APIs") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NeonViolet,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonViolet,
                            unfocusedBorderColor = CyberSurfaceStroke
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Job Status:", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = isCompleted,
                            onClick = { isCompleted = true },
                            label = { Text("COMPLETED") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SuccessBadgeBg,
                                selectedLabelColor = NeonGreen
                            )
                        )
                        FilterChip(
                            selected = !isCompleted,
                            onClick = { isCompleted = false },
                            label = { Text("IN PROGRESS") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = InfoBadgeBg,
                                selectedLabelColor = NeonCyan
                            )
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        fileName,
                        packageName,
                        appName,
                        criticalCount.toIntOrNull() ?: 0,
                        highCount.toIntOrNull() ?: 0,
                        apisCount.toIntOrNull() ?: 0,
                        if (isCompleted) "COMPLETED" else "IN_PROGRESS"
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = CyberNavyDark
                )
            ) {
                Text("Save to Room DB")
            }
        }
    )
}
