package com.example.data.repository

import com.example.data.local.SavedBusinessEntity
import com.example.data.local.SavedHiringPostEntity
import com.example.data.local.SkillRadarDao
import com.example.data.model.BusinessMatch
import com.example.data.model.HiringPost
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray

class SkillRadarRepository(
    private val dao: SkillRadarDao,
    private val geminiService: GeminiService
) {

    val savedBusinesses: Flow<List<SavedBusinessEntity>> = dao.getAllSavedBusinesses()
    val savedHiringPosts: Flow<List<SavedHiringPostEntity>> = dao.getAllSavedHiringPosts()
    val savedBusinessIds: Flow<List<String>> = dao.getAllSavedBusinessIds()
    val savedHiringPostIds: Flow<List<String>> = dao.getAllSavedHiringPostIds()

    val isApiKeyConfigured: Boolean
        get() = geminiService.isApiKeyConfigured

    suspend fun searchBusinesses(
        skill: String,
        country: String,
        city: String = "",
        industry: String = ""
    ): List<BusinessMatch> {
        return geminiService.searchAndAnalyzeBusinesses(skill, country, city, industry)
    }

    suspend fun searchHiringAndNeeds(
        skill: String,
        country: String,
        filter: String = ""
    ): List<HiringPost> {
        return geminiService.searchHiringAndNeeds(skill, country, filter)
    }

    suspend fun generatePitch(
        targetName: String,
        targetDetails: String,
        skill: String,
        tone: String
    ): String {
        return geminiService.generateCustomPitch(targetName, targetDetails, skill, tone)
    }

    suspend fun saveBusiness(business: BusinessMatch) {
        val deliverablesJson = JSONArray(business.deliverables).toString()
        val channelsJson = JSONArray(business.contactChannels).toString()

        val entity = SavedBusinessEntity(
            id = business.id,
            name = business.name,
            category = business.category,
            city = business.city,
            country = business.country,
            address = business.address,
            googleMapsQuery = business.googleMapsQuery,
            matchScore = business.matchScore,
            currentGap = business.currentGap,
            howWeHelp = business.howWeHelp,
            deliverablesJson = deliverablesJson,
            expectedRoi = business.expectedRoi,
            estimatedPricing = business.estimatedPricing,
            outreachPitch = business.outreachPitch,
            contactChannelsJson = channelsJson,
            phone = business.phone,
            website = business.website,
            status = "Prospect",
            notes = "",
            savedAt = System.currentTimeMillis()
        )
        dao.insertBusiness(entity)
    }

    suspend fun deleteBusiness(id: String) {
        dao.deleteBusinessById(id)
    }

    suspend fun updateBusinessStatus(id: String, status: String) {
        dao.updateBusinessStatus(id, status)
    }

    suspend fun updateBusinessNotes(id: String, notes: String) {
        dao.updateBusinessNotes(id, notes)
    }

    suspend fun saveHiringPost(post: HiringPost) {
        val reqJson = JSONArray(post.requirements).toString()
        val entity = SavedHiringPostEntity(
            id = post.id,
            title = post.title,
            company = post.company,
            platformSource = post.platformSource,
            postedAgo = post.postedAgo,
            location = post.location,
            country = post.country,
            budget = post.budget,
            postType = post.postType,
            description = post.description,
            requirementsJson = reqJson,
            howToPitch = post.howToPitch,
            readyPitchProposal = post.readyPitchProposal,
            status = "Saved",
            notes = "",
            savedAt = System.currentTimeMillis()
        )
        dao.insertHiringPost(entity)
    }

    suspend fun deleteHiringPost(id: String) {
        dao.deleteHiringPostById(id)
    }

    suspend fun updateHiringPostStatus(id: String, status: String) {
        dao.updateHiringPostStatus(id, status)
    }

    suspend fun updateHiringPostNotes(id: String, notes: String) {
        dao.updateHiringPostNotes(id, notes)
    }
}
