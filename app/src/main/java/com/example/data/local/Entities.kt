package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_businesses")
data class SavedBusinessEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val city: String,
    val country: String,
    val address: String,
    val googleMapsQuery: String,
    val matchScore: Int,
    val currentGap: String,
    val howWeHelp: String,
    val deliverablesJson: String,
    val expectedRoi: String,
    val estimatedPricing: String,
    val outreachPitch: String,
    val contactChannelsJson: String,
    val phone: String,
    val website: String,
    val status: String = "Prospect",
    val notes: String = "",
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_hiring_posts")
data class SavedHiringPostEntity(
    @PrimaryKey val id: String,
    val title: String,
    val company: String,
    val platformSource: String,
    val postedAgo: String,
    val location: String,
    val country: String,
    val budget: String,
    val postType: String,
    val description: String,
    val requirementsJson: String,
    val howToPitch: String,
    val readyPitchProposal: String,
    val status: String = "Saved",
    val notes: String = "",
    val savedAt: Long = System.currentTimeMillis()
)
