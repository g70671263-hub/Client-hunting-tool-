package com.example.data.model

data class BusinessMatch(
    val id: String,
    val name: String,
    val category: String,
    val city: String,
    val country: String,
    val address: String,
    val googleMapsQuery: String,
    val matchScore: Int, // e.g. 95
    val currentGap: String,
    val howWeHelp: String,
    val deliverables: List<String>,
    val expectedRoi: String,
    val estimatedPricing: String,
    val outreachPitch: String,
    val contactChannels: List<String>,
    val phone: String = "",
    val website: String = "",
    val isSaved: Boolean = false
)

data class HiringPost(
    val id: String,
    val title: String,
    val company: String,
    val platformSource: String, // LinkedIn, Twitter/X, Upwork, Local Board, etc.
    val postedAgo: String,
    val location: String,
    val country: String,
    val budget: String,
    val postType: String, // "Contract Gig", "Urgent Need", "Retainer", "Full-time"
    val description: String,
    val requirements: List<String>,
    val howToPitch: String,
    val readyPitchProposal: String,
    val isSaved: Boolean = false
)

enum class LeadStatus(val label: String) {
    PROSPECT("Prospect"),
    PITCHED("Pitched"),
    DISCUSSION("In Discussion"),
    WON("Closed Won"),
    ARCHIVED("Archived")
}

data class PresetSkill(
    val title: String,
    val category: String,
    val iconName: String
)

data class PresetCountry(
    val name: String,
    val flag: String,
    val topCities: List<String>
)
