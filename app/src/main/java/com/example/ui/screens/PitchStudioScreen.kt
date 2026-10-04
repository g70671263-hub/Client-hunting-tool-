package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Presets
import com.example.ui.SkillRadarViewModel
import com.example.ui.components.copyToClipboard
import com.example.ui.components.sendEmailIntent
import com.example.ui.components.shareText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PitchStudioScreen(
    viewModel: SkillRadarViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val targetName by viewModel.pitchTargetName.collectAsState()
    val targetContext by viewModel.pitchTargetContext.collectAsState()
    val skill by viewModel.pitchSkill.collectAsState()
    val activeTone by viewModel.pitchTone.collectAsState()
    val generatedPitch by viewModel.generatedPitch.collectAsState()
    val isGenerating by viewModel.isGeneratingPitch.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("pitch_studio_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "AI OUTREACH & PROPOSAL STUDIO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "High-Converting Pitch Crafter",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Generate tailored cold emails, LinkedIn messages, and proposals",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = targetName,
                        onValueChange = { viewModel.setPitchTarget(it, targetContext, skill, generatedPitch) },
                        label = { Text("Target Business or Client Name") },
                        placeholder = { Text("e.g. Bella Roma Trattoria or Apex SaaS") },
                        leadingIcon = {
                            Icon(Icons.Default.Business, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pitch_target_name_input")
                    )

                    OutlinedTextField(
                        value = skill,
                        onValueChange = { viewModel.setPitchTarget(targetName, targetContext, it, generatedPitch) },
                        label = { Text("Skill / Value Proposition to Pitch") },
                        leadingIcon = {
                            Icon(Icons.Default.Psychology, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Select Outreach Angle / Tone:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Presets.outreachTones.forEach { tone ->
                            val isSelected = activeTone == tone
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setPitchTone(tone) },
                                label = { Text(tone, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.generatePitch() },
                        enabled = !isGenerating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_pitch_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Crafting Personalized Pitch...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Pitch with AI")
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Generated Pitch Output Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Draft Pitch & Cold Message",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (generatedPitch.isNotBlank()) {
                            Text(
                                text = "${generatedPitch.split("\\s+".toRegex()).size} words",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedTextField(
                        value = generatedPitch,
                        onValueChange = { viewModel.updatePitchContent(it) },
                        placeholder = { Text("Your generated pitch will appear here. Tap 'Generate Pitch with AI' above or select a business or hiring post.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .testTag("pitch_content_editor")
                    )

                    // Outreach Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (generatedPitch.isNotBlank()) {
                                    copyToClipboard(context, "Pitch", generatedPitch)
                                }
                            },
                            enabled = generatedPitch.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (generatedPitch.isNotBlank()) {
                                    sendEmailIntent(
                                        context = context,
                                        recipient = "",
                                        subject = "Collaboration inquiry regarding $skill",
                                        body = generatedPitch
                                    )
                                }
                            },
                            enabled = generatedPitch.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Email", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (generatedPitch.isNotBlank()) {
                                    shareText(context, "Outreach Pitch for $targetName", generatedPitch)
                                }
                            },
                            enabled = generatedPitch.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
