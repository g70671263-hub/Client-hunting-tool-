package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.remote.GeminiService
import com.example.data.repository.SkillRadarRepository
import com.example.ui.SkillRadarViewModel
import com.example.ui.SkillRadarViewModelFactory
import com.example.ui.screens.BusinessRadarScreen
import com.example.ui.screens.HiringRadarScreen
import com.example.ui.screens.LeadCrmScreen
import com.example.ui.screens.PitchStudioScreen
import com.example.ui.theme.SkillRadarTheme
import com.example.ui.theme.SuccessGreen

class MainActivity : ComponentActivity() {

    private val viewModel: SkillRadarViewModel by viewModels {
        val database = AppDatabase.getInstance(applicationContext)
        val geminiService = GeminiService()
        val repository = SkillRadarRepository(database.skillRadarDao(), geminiService)
        SkillRadarViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillRadarTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    BUSINESS_RADAR("Biz Radar", Icons.Default.Radar),
    HIRING_RADAR("Hiring Radar", Icons.Default.Work),
    LEAD_CRM("Pipeline CRM", Icons.Default.FolderSpecial),
    PITCH_STUDIO("Pitch Studio", Icons.Default.Create)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: SkillRadarViewModel) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val savedBusinesses by viewModel.savedBusinesses.collectAsState()
    val savedHiringPosts by viewModel.savedHiringPosts.collectAsState()
    val totalSaved = savedBusinesses.size + savedHiringPosts.size

    // Handle back button: if on a secondary tab, return to Biz Radar first
    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SkillRadar",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.15f),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "B2B AI",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationTab.values().forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(tab.title, fontSize = 11.sp) },
                        icon = {
                            if (tab == NavigationTab.LEAD_CRM && totalSaved > 0) {
                                BadgedBox(badge = { Badge { Text("$totalSaved") } }) {
                                    Icon(tab.icon, contentDescription = tab.title)
                                }
                            } else {
                                Icon(tab.icon, contentDescription = tab.title)
                            }
                        },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> BusinessRadarScreen(
                    viewModel = viewModel,
                    onNavigateToPitchStudio = { name, context, skill, pitch ->
                        viewModel.setPitchTarget(name, context, skill, pitch)
                        selectedTab = 3
                    }
                )
                1 -> HiringRadarScreen(
                    viewModel = viewModel,
                    onNavigateToPitchStudio = { name, context, skill, pitch ->
                        viewModel.setPitchTarget(name, context, skill, pitch)
                        selectedTab = 3
                    }
                )
                2 -> LeadCrmScreen(
                    viewModel = viewModel,
                    onNavigateToPitchStudio = { name, context, skill, pitch ->
                        viewModel.setPitchTarget(name, context, skill, pitch)
                        selectedTab = 3
                    }
                )
                3 -> PitchStudioScreen(viewModel = viewModel)
            }
        }
    }
}
