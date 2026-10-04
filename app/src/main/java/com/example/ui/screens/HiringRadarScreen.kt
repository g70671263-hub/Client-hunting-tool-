package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.data.Presets
import com.example.data.model.HiringPost
import com.example.ui.SkillRadarViewModel
import com.example.ui.components.SearchableCountryPickerDialog
import com.example.ui.components.copyToClipboard
import com.example.ui.components.shareText
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HiringRadarScreen(
    viewModel: SkillRadarViewModel,
    onNavigateToPitchStudio: (name: String, context: String, skill: String, pitch: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val skill by viewModel.hiringSkill.collectAsState()
    val country by viewModel.hiringCountry.collectAsState()
    val activeFilter by viewModel.hiringFilter.collectAsState()
    val isSearching by viewModel.isSearchingHiring.collectAsState()
    val results by viewModel.hiringResults.collectAsState()
    val error by viewModel.hiringSearchError.collectAsState()
    val savedIds by viewModel.savedHiringPostIds.collectAsState()
    val selectedPost by viewModel.selectedHiringForDetail.collectAsState()
    var showCountryPicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("hiring_radar_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Header Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.FlashOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE HIRING & CLIENT NEED RADAR",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Client Requests & Gigs",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Search active hiring calls, contract gigs, and RFPs matching your skill",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search Controls Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = skill,
                        onValueChange = { viewModel.setHiringSkill(it) },
                        label = { Text("Search by Skill") },
                        placeholder = { Text("e.g. Next.js, SEO, AI Automation...") },
                        leadingIcon = {
                            Icon(Icons.Default.Work, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            if (skill.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setHiringSkill("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hiring_skill_input")
                    )

                    OutlinedTextField(
                        value = country,
                        onValueChange = { viewModel.setHiringCountry(it) },
                        label = { Text("Country / Remote Scope (126+)") },
                        leadingIcon = {
                            val currentPreset = Presets.popularCountries.find { it.name.equals(country, ignoreCase = true) }
                            if (currentPreset != null) {
                                Text(currentPreset.flag, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp, end = 4.dp))
                            } else {
                                Icon(Icons.Default.Public, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = { showCountryPicker = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select from 126+ Countries")
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hiring_country_input")
                    )

                    // Filter Chips (All, Urgent Need, Contract Gig, Retainer, etc.)
                    Text(
                        text = "Opportunity Type:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(Presets.hiringFilters) { filterOption ->
                            val isSelected = activeFilter == filterOption
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setHiringFilter(filterOption) },
                                label = { Text(filterOption, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.searchHiring() },
                        enabled = !isSearching,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("search_hiring_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scanning Hiring Posts...")
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Search Hiring & Needs")
                        }
                    }
                }
            }
        }

        // Results Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Opportunities Found (${results.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Error Banner
        if (error != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(error ?: "", color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }

        // Hiring Post Items Feed
        items(results, key = { it.id }) { post ->
            val isSaved = savedIds.contains(post.id)
            HiringPostCard(
                post = post,
                isSaved = isSaved,
                onSaveClick = { viewModel.toggleSaveHiringPost(post) },
                onCopyPitch = { copyToClipboard(context, "Proposal Draft", post.readyPitchProposal) },
                onPitchStudioClick = {
                    onNavigateToPitchStudio(
                        post.company,
                        "${post.title} (${post.postType}). Requirements: ${post.requirements.joinToString(", ")}",
                        skill,
                        post.readyPitchProposal
                    )
                },
                onCardClick = { viewModel.setSelectedHiringPost(post) }
            )
        }
    }

    // Detail Dialog
    if (selectedPost != null) {
        val p = selectedPost!!
        val isSaved = savedIds.contains(p.id)
        HiringDetailDialog(
            post = p,
            isSaved = isSaved,
            onDismiss = { viewModel.setSelectedHiringPost(null) },
            onSaveClick = { viewModel.toggleSaveHiringPost(p) },
            onCopyPitch = { copyToClipboard(context, "Proposal", p.readyPitchProposal) },
            onPitchStudioClick = {
                viewModel.setSelectedHiringPost(null)
                onNavigateToPitchStudio(
                    p.company,
                    "${p.title} (${p.postType})",
                    skill,
                    p.readyPitchProposal
                )
            }
        )
    }

    if (showCountryPicker) {
        SearchableCountryPickerDialog(
            selectedCountry = country,
            onCountrySelected = { selectedName, _ ->
                viewModel.setHiringCountry(selectedName)
                showCountryPicker = false
            },
            onDismiss = { showCountryPicker = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HiringPostCard(
    post: HiringPost,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onCopyPitch: () -> Unit,
    onPitchStudioClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick() }
            .testTag("hiring_card_${post.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Source & Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = getPlatformColor(post.platformSource).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = post.platformSource,
                        style = MaterialTheme.typography.labelSmall,
                        color = getPlatformColor(post.platformSource),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.postedAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isSaved) "Saved" else "Save",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Title & Company
            Column {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = post.company,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = post.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Budget & Type Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = SuccessGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = post.budget,
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = post.postType,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Brief Description
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            // Tactical Pitch Angle Box
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "HOW TO PITCH THIS CLIENT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = post.howToPitch,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyPitch,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Proposal", fontSize = 12.sp)
                }

                Button(
                    onClick = onPitchStudioClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Customize Pitch", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun HiringDetailDialog(
    post: HiringPost,
    isSaved: Boolean,
    onDismiss: () -> Unit,
    onSaveClick: () -> Unit,
    onCopyPitch: () -> Unit,
    onPitchStudioClick: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "${post.platformSource} • ${post.postedAgo}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${post.company} • ${post.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Budget: ${post.budget}",
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Text(
                            text = post.postType,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                item {
                    Text(
                        text = "Full Description:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = post.description, style = MaterialTheme.typography.bodySmall)
                }

                item {
                    Text(
                        text = "Requirements:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    post.requirements.forEach { req ->
                        Text(text = "• $req", style = MaterialTheme.typography.bodySmall)
                    }
                }

                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Pre-Crafted Winning Proposal Draft",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = post.readyPitchProposal, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onPitchStudioClick) {
                Text("Open in Pitch Studio")
            }
        },
        dismissButton = {
            Row {
                OutlinedButton(onClick = onCopyPitch) {
                    Text("Copy Draft")
                }
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

fun getPlatformColor(source: String): androidx.compose.ui.graphics.Color {
    return when {
        source.contains("LinkedIn", ignoreCase = true) -> InfoBlue
        source.contains("Twitter", ignoreCase = true) || source.contains("X", ignoreCase = true) -> PurpleAccent
        source.contains("Upwork", ignoreCase = true) -> SuccessGreen
        else -> WarningOrange
    }
}
