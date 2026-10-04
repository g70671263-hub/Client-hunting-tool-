package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Presets
import com.example.data.model.BusinessMatch
import com.example.ui.SkillRadarViewModel
import com.example.ui.components.MatchScoreBadge
import com.example.ui.components.SearchableCountryPickerDialog
import com.example.ui.components.copyToClipboard
import com.example.ui.components.launchGoogleMaps
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BusinessRadarScreen(
    viewModel: SkillRadarViewModel,
    onNavigateToPitchStudio: (name: String, context: String, skill: String, pitch: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val skill by viewModel.searchSkill.collectAsState()
    val country by viewModel.searchCountry.collectAsState()
    val city by viewModel.searchCity.collectAsState()
    val industry by viewModel.searchIndustry.collectAsState()
    val isSearching by viewModel.isSearchingBusinesses.collectAsState()
    val results by viewModel.businessResults.collectAsState()
    val error by viewModel.businessSearchError.collectAsState()
    val savedIds by viewModel.savedBusinessIds.collectAsState()
    val selectedBusiness by viewModel.selectedBusinessForDetail.collectAsState()

    var showFilters by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }
    var industryMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("business_radar_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.biz_radar_hero_1791033703159),
                    contentDescription = "Business Radar Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color.Transparent,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "GOOGLE MAPS B2B RADAR",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Match Your Skills to Businesses",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Analyze gaps in local enterprises & discover how you can help",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Search Controls Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Skill Input
                    OutlinedTextField(
                        value = skill,
                        onValueChange = { viewModel.setSkill(it) },
                        label = { Text("Your Skill or Service") },
                        placeholder = { Text("e.g. Web Development & SEO, AI Chatbots...") },
                        leadingIcon = {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            if (skill.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSkill("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear skill")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("skill_input_field")
                    )

                    // Popular Preset Skill Chips
                    Text(
                        text = "Quick Select Skill:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(Presets.popularSkills) { preset ->
                            val isSelected = skill.equals(preset.title, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSkill(preset.title) },
                                label = { Text(preset.title, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    // Country & Location Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Country Field with Searchable 126+ Country Selector
                        OutlinedTextField(
                            value = country,
                            onValueChange = { viewModel.setCountry(it) },
                            label = { Text("Country (126+)") },
                            leadingIcon = {
                                val currentPreset = Presets.popularCountries.find { it.name.equals(country, ignoreCase = true) }
                                if (currentPreset != null) {
                                    Text(currentPreset.flag, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp, end = 4.dp))
                                } else {
                                    Icon(Icons.Default.Public, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            },
                            trailingIcon = {
                                IconButton(onClick = { showCountryPicker = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select from 126+ Countries")
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("country_input_field")
                        )

                        // City Input
                        OutlinedTextField(
                            value = city,
                            onValueChange = { viewModel.setCity(it) },
                            label = { Text("City / Region") },
                            placeholder = { Text("All Cities") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("city_input_field")
                        )
                    }

                    // Toggle Advanced Filters (Industry Niche)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showFilters = !showFilters },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (showFilters) "Hide Industry Filter" else "Filter by Industry (${industry})")
                        }

                        if (industry != "All Industries") {
                            TextButton(onClick = { viewModel.setIndustry("All Industries") }) {
                                Text("Reset Filter", fontSize = 12.sp)
                            }
                        }
                    }

                    AnimatedVisibility(visible = showFilters) {
                        ExposedDropdownMenuBox(
                            expanded = industryMenuExpanded,
                            onExpandedChange = { industryMenuExpanded = !industryMenuExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = industry,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Target Business Industry") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = industryMenuExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            )

                            ExposedDropdownMenu(
                                expanded = industryMenuExpanded,
                                onDismissRequest = { industryMenuExpanded = false }
                            ) {
                                Presets.businessIndustries.forEach { ind ->
                                    DropdownMenuItem(
                                        text = { Text(ind) },
                                        onClick = {
                                            viewModel.setIndustry(ind)
                                            industryMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Main Action CTA Button
                    Button(
                        onClick = { viewModel.searchBusinesses() },
                        enabled = !isSearching,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("scan_businesses_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Analyzing Businesses on Maps...")
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan Businesses & Analyze Fit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Summary Bar & Results Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Identified Businesses (${results.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (results.isNotEmpty()) {
                    val avgFit = results.map { it.matchScore }.average().toInt()
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Avg Opportunity: $avgFit%",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Error message if any
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

        // Business Results Feed
        items(results, key = { it.id }) { business ->
            val isSaved = savedIds.contains(business.id)
            BusinessMatchCard(
                business = business,
                isSaved = isSaved,
                onSaveClick = { viewModel.toggleSaveBusiness(business) },
                onMapsClick = { launchGoogleMaps(context, business.googleMapsQuery) },
                onPitchClick = {
                    onNavigateToPitchStudio(
                        business.name,
                        "${business.category} in ${business.city}, ${business.country}. Gap: ${business.currentGap}",
                        skill,
                        business.outreachPitch
                    )
                },
                onCardClick = { viewModel.setSelectedBusiness(business) }
            )
        }
    }

    // Full Detail Modal Dialog
    if (selectedBusiness != null) {
        val b = selectedBusiness!!
        val isSaved = savedIds.contains(b.id)
        BusinessDetailDialog(
            business = b,
            isSaved = isSaved,
            onDismiss = { viewModel.setSelectedBusiness(null) },
            onSaveClick = { viewModel.toggleSaveBusiness(b) },
            onMapsClick = { launchGoogleMaps(context, b.googleMapsQuery) },
            onPitchClick = {
                viewModel.setSelectedBusiness(null)
                onNavigateToPitchStudio(
                    b.name,
                    "${b.category} in ${b.city}, ${b.country}. Gap: ${b.currentGap}",
                    skill,
                    b.outreachPitch
                )
            }
        )
    }

    if (showCountryPicker) {
        SearchableCountryPickerDialog(
            selectedCountry = country,
            onCountrySelected = { selectedName, primaryCity ->
                viewModel.setCountry(selectedName, primaryCity)
                showCountryPicker = false
            },
            onDismiss = { showCountryPicker = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BusinessMatchCard(
    business: BusinessMatch,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onMapsClick: () -> Unit,
    onPitchClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick() }
            .testTag("business_card_${business.name.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Category, Name, Match Badge & Save Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = business.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = business.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${business.city}, ${business.country}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MatchScoreBadge(score = business.matchScore)
                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isSaved) "Saved" else "Save",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Gap Analysis Box (Red/Amber warning tint)
            Surface(
                color = WarningOrange.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "IDENTIFIED GAP / DEFICIENCY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = business.currentGap,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // How You Can Help Box (Teal/Green tint)
            Surface(
                color = SuccessGreen.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HOW YOUR SKILL CAN HELP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = business.howWeHelp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Value & Deliverables Snippet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Est. Value: ${business.estimatedPricing}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onMapsClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Google Maps", fontSize = 12.sp)
                }

                Button(
                    onClick = onPitchClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pitch Client", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun BusinessDetailDialog(
    business: BusinessMatch,
    isSaved: Boolean,
    onDismiss: () -> Unit,
    onSaveClick: () -> Unit,
    onMapsClick: () -> Unit,
    onPitchClick: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = business.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = business.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${business.address}, ${business.city}, ${business.country}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MatchScoreBadge(score = business.matchScore)
                        Text(
                            text = business.estimatedPricing,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                item {
                    Text(
                        text = "Current Operational Gap",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = business.currentGap, style = MaterialTheme.typography.bodySmall)
                }

                item {
                    Text(
                        text = "Tailored Solution Plan",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Text(text = business.howWeHelp, style = MaterialTheme.typography.bodySmall)
                }

                item {
                    Text(
                        text = "Proposed Deliverables",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    business.deliverables.forEach { del ->
                        Text(text = "• $del", style = MaterialTheme.typography.bodySmall)
                    }
                }

                item {
                    Text(
                        text = "Expected Business ROI",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = business.expectedRoi, style = MaterialTheme.typography.bodySmall)
                }

                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ready Cold Outreach Hook",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { copyToClipboard(context, "Pitch", business.outreachPitch) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = business.outreachPitch, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onPitchClick) {
                Text("Craft Full Pitch")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(onClick = onMapsClick) {
                    Text("Maps")
                }
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}
