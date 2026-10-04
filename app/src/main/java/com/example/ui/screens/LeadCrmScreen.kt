package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedBusinessEntity
import com.example.data.local.SavedHiringPostEntity
import com.example.ui.SkillRadarViewModel
import com.example.ui.components.MatchScoreBadge
import com.example.ui.components.launchGoogleMaps
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

val crmStatuses = listOf("Prospect", "Pitched", "In Discussion", "Closed Won", "Archived")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadCrmScreen(
    viewModel: SkillRadarViewModel,
    onNavigateToPitchStudio: (name: String, context: String, skill: String, pitch: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savedBusinesses by viewModel.savedBusinesses.collectAsState()
    val savedHiringPosts by viewModel.savedHiringPosts.collectAsState()
    val currentTab by viewModel.crmTab.collectAsState()
    val statusFilter by viewModel.crmStatusFilter.collectAsState()

    var notesDialogTarget by remember { mutableStateOf<Pair<String, String>?>(null) } // id to currentNotes
    var isBusinessNotes by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("lead_crm_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Lead CRM & Pipeline",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Track your client outreach, pipeline status, and proposal stages",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Top Tabs: Businesses vs Hiring Posts
        PrimaryTabRow(
            selectedTabIndex = if (currentTab == "Businesses") 0 else 1,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = currentTab == "Businesses",
                onClick = { viewModel.setCrmTab("Businesses") },
                text = { Text("Saved Businesses (${savedBusinesses.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Business, contentDescription = null) }
            )
            Tab(
                selected = currentTab == "Hiring Posts",
                onClick = { viewModel.setCrmTab("Hiring Posts") },
                text = { Text("Hiring Posts (${savedHiringPosts.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Work, contentDescription = null) }
            )
        }

        // Status Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = statusFilter == "All",
                    onClick = { viewModel.setCrmStatusFilter("All") },
                    label = { Text("All", fontSize = 12.sp) }
                )
            }
            items(crmStatuses) { st ->
                FilterChip(
                    selected = statusFilter == st,
                    onClick = { viewModel.setCrmStatusFilter(st) },
                    label = { Text(st, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        // Content
        if (currentTab == "Businesses") {
            val filteredBusinesses = savedBusinesses.filter {
                statusFilter == "All" || it.status.equals(statusFilter, ignoreCase = true)
            }

            if (filteredBusinesses.isEmpty()) {
                EmptyCrmPlaceholder(
                    title = "No Saved Businesses",
                    subtitle = "Scan and bookmark businesses in the Business Radar tab to track them here!"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(filteredBusinesses, key = { it.id }) { b ->
                        SavedBusinessItemCard(
                            business = b,
                            onMapsClick = { launchGoogleMaps(context, b.googleMapsQuery) },
                            onPitchClick = {
                                onNavigateToPitchStudio(
                                    b.name,
                                    "${b.category} in ${b.city}, ${b.country}. Gap: ${b.currentGap}",
                                    "Skill",
                                    b.outreachPitch
                                )
                            },
                            onStatusChange = { newStatus -> viewModel.updateBusinessStatus(b.id, newStatus) },
                            onEditNotes = {
                                isBusinessNotes = true
                                notesDialogTarget = b.id to b.notes
                            },
                            onDelete = { viewModel.deleteSavedBusiness(b.id) }
                        )
                    }
                }
            }
        } else {
            val filteredPosts = savedHiringPosts.filter {
                statusFilter == "All" || it.status.equals(statusFilter, ignoreCase = true)
            }

            if (filteredPosts.isEmpty()) {
                EmptyCrmPlaceholder(
                    title = "No Saved Hiring Posts",
                    subtitle = "Bookmark client posts from the Hiring Radar tab to manage your proposals here!"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(filteredPosts, key = { it.id }) { p ->
                        SavedHiringPostItemCard(
                            post = p,
                            onPitchClick = {
                                onNavigateToPitchStudio(
                                    p.company,
                                    "${p.title} (${p.postType})",
                                    "Skill",
                                    p.readyPitchProposal
                                )
                            },
                            onStatusChange = { newStatus -> viewModel.updateHiringPostStatus(p.id, newStatus) },
                            onEditNotes = {
                                isBusinessNotes = false
                                notesDialogTarget = p.id to p.notes
                            },
                            onDelete = { viewModel.deleteSavedHiringPost(p.id) }
                        )
                    }
                }
            }
        }
    }

    // Notes Edit Dialog
    if (notesDialogTarget != null) {
        val (targetId, currentNotes) = notesDialogTarget!!
        var editedNotes by remember(targetId) { mutableStateOf(currentNotes) }

        AlertDialog(
            onDismissRequest = { notesDialogTarget = null },
            title = { Text("Client Notes & Follow-up Log") },
            text = {
                OutlinedTextField(
                    value = editedNotes,
                    onValueChange = { editedNotes = it },
                    placeholder = { Text("Log meeting notes, agreed deliverables, or follow-up dates...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (isBusinessNotes) {
                        viewModel.updateBusinessNotes(targetId, editedNotes)
                    } else {
                        viewModel.updateHiringPostNotes(targetId, editedNotes)
                    }
                    notesDialogTarget = null
                }) {
                    Text("Save Notes")
                }
            },
            dismissButton = {
                TextButton(onClick = { notesDialogTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedBusinessItemCard(
    business: SavedBusinessEntity,
    onMapsClick: () -> Unit,
    onPitchClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    onEditNotes: () -> Unit,
    onDelete: () -> Unit
) {
    var statusMenuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = business.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${business.category} • ${business.city}, ${business.country}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                MatchScoreBadge(score = business.matchScore)
            }

            // Status Selector and Notes Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = statusMenuExpanded,
                    onExpandedChange = { statusMenuExpanded = !statusMenuExpanded }
                ) {
                    Surface(
                        color = getStatusColor(business.status).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Stage: ${business.status}",
                                color = getStatusColor(business.status),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        crmStatuses.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    onStatusChange(st)
                                    statusMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEditNotes, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.EditNote, contentDescription = "Edit Notes")
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (business.notes.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${business.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onMapsClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View on Maps", fontSize = 11.sp)
                }

                Button(
                    onClick = onPitchClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Outreach Pitch", fontSize = 11.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedHiringPostItemCard(
    post: SavedHiringPostEntity,
    onPitchClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    onEditNotes: () -> Unit,
    onDelete: () -> Unit
) {
    var statusMenuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${post.company} • ${post.platformSource}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = SuccessGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = post.budget,
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Status Selector and Notes Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = statusMenuExpanded,
                    onExpandedChange = { statusMenuExpanded = !statusMenuExpanded }
                ) {
                    Surface(
                        color = getStatusColor(post.status).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    ) {
                        Text(
                            text = "Stage: ${post.status}",
                            color = getStatusColor(post.status),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    ExposedDropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        crmStatuses.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    onStatusChange(st)
                                    statusMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEditNotes, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.EditNote, contentDescription = "Edit Notes")
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (post.notes.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${post.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onPitchClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Customize & Send Proposal", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun EmptyCrmPlaceholder(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

fun getStatusColor(status: String): androidx.compose.ui.graphics.Color {
    return when (status.lowercase()) {
        "closed won", "won", "hired" -> SuccessGreen
        "in discussion", "interviewing" -> WarningOrange
        "pitched", "applied" -> androidx.compose.ui.graphics.Color(0xFF3B82F6)
        else -> androidx.compose.ui.graphics.Color(0xFF6B7280)
    }
}
