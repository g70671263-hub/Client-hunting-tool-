package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Presets
import com.example.data.local.SavedBusinessEntity
import com.example.data.local.SavedHiringPostEntity
import com.example.data.model.BusinessMatch
import com.example.data.model.HiringPost
import com.example.data.repository.SkillRadarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SkillRadarViewModel(
    private val repository: SkillRadarRepository
) : ViewModel() {

    // --- Search Businesses State ---
    private val _searchSkill = MutableStateFlow("Web Development & SEO")
    val searchSkill: StateFlow<String> = _searchSkill.asStateFlow()

    private val _searchCountry = MutableStateFlow("United States")
    val searchCountry: StateFlow<String> = _searchCountry.asStateFlow()

    private val _searchCity = MutableStateFlow("New York")
    val searchCity: StateFlow<String> = _searchCity.asStateFlow()

    private val _searchIndustry = MutableStateFlow("All Industries")
    val searchIndustry: StateFlow<String> = _searchIndustry.asStateFlow()

    private val _isSearchingBusinesses = MutableStateFlow(false)
    val isSearchingBusinesses: StateFlow<Boolean> = _isSearchingBusinesses.asStateFlow()

    private val _businessResults = MutableStateFlow<List<BusinessMatch>>(emptyList())
    val businessResults: StateFlow<List<BusinessMatch>> = _businessResults.asStateFlow()

    private val _businessSearchError = MutableStateFlow<String?>(null)
    val businessSearchError: StateFlow<String?> = _businessSearchError.asStateFlow()

    private val _selectedBusinessForDetail = MutableStateFlow<BusinessMatch?>(null)
    val selectedBusinessForDetail: StateFlow<BusinessMatch?> = _selectedBusinessForDetail.asStateFlow()

    // --- Hiring & Needs State ---
    private val _hiringSkill = MutableStateFlow("Web Development & SEO")
    val hiringSkill: StateFlow<String> = _hiringSkill.asStateFlow()

    private val _hiringCountry = MutableStateFlow("United States")
    val hiringCountry: StateFlow<String> = _hiringCountry.asStateFlow()

    private val _hiringFilter = MutableStateFlow("All")
    val hiringFilter: StateFlow<String> = _hiringFilter.asStateFlow()

    private val _isSearchingHiring = MutableStateFlow(false)
    val isSearchingHiring: StateFlow<Boolean> = _isSearchingHiring.asStateFlow()

    private val _hiringResults = MutableStateFlow<List<HiringPost>>(emptyList())
    val hiringResults: StateFlow<List<HiringPost>> = _hiringResults.asStateFlow()

    private val _hiringSearchError = MutableStateFlow<String?>(null)
    val hiringSearchError: StateFlow<String?> = _hiringSearchError.asStateFlow()

    private val _selectedHiringForDetail = MutableStateFlow<HiringPost?>(null)
    val selectedHiringForDetail: StateFlow<HiringPost?> = _selectedHiringForDetail.asStateFlow()

    // --- CRM / Saved Leads ---
    val savedBusinesses: StateFlow<List<SavedBusinessEntity>> = repository.savedBusinesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedHiringPosts: StateFlow<List<SavedHiringPostEntity>> = repository.savedHiringPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedBusinessIds: StateFlow<Set<String>> = repository.savedBusinessIds
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val savedHiringPostIds: StateFlow<Set<String>> = repository.savedHiringPostIds
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _crmTab = MutableStateFlow("Businesses") // "Businesses" or "Hiring Posts"
    val crmTab: StateFlow<String> = _crmTab.asStateFlow()

    private val _crmStatusFilter = MutableStateFlow("All")
    val crmStatusFilter: StateFlow<String> = _crmStatusFilter.asStateFlow()

    // --- Pitch Studio State ---
    private val _pitchTargetName = MutableStateFlow("")
    val pitchTargetName: StateFlow<String> = _pitchTargetName.asStateFlow()

    private val _pitchTargetContext = MutableStateFlow("")
    val pitchTargetContext: StateFlow<String> = _pitchTargetContext.asStateFlow()

    private val _pitchSkill = MutableStateFlow("Web Development & SEO")
    val pitchSkill: StateFlow<String> = _pitchSkill.asStateFlow()

    private val _pitchTone = MutableStateFlow(Presets.outreachTones.first())
    val pitchTone: StateFlow<String> = _pitchTone.asStateFlow()

    private val _generatedPitch = MutableStateFlow("")
    val generatedPitch: StateFlow<String> = _generatedPitch.asStateFlow()

    private val _isGeneratingPitch = MutableStateFlow(false)
    val isGeneratingPitch: StateFlow<Boolean> = _isGeneratingPitch.asStateFlow()

    val isApiKeyConfigured: Boolean
        get() = repository.isApiKeyConfigured

    init {
        // Automatically perform initial discovery scan so the user sees results instantly
        searchBusinesses()
        searchHiring()
    }

    // --- Business Search Actions ---
    fun setSkill(skill: String) {
        _searchSkill.value = skill
        _hiringSkill.value = skill
        _pitchSkill.value = skill
    }

    fun setCountry(country: String, defaultCity: String = "") {
        _searchCountry.value = country
        _hiringCountry.value = country
        if (defaultCity.isNotBlank()) {
            _searchCity.value = defaultCity
        }
    }

    fun setCity(city: String) {
        _searchCity.value = city
    }

    fun setIndustry(industry: String) {
        _searchIndustry.value = industry
    }

    fun setSelectedBusiness(business: BusinessMatch?) {
        _selectedBusinessForDetail.value = business
    }

    fun searchBusinesses() {
        val skill = _searchSkill.value.trim()
        val country = _searchCountry.value.trim()
        val city = _searchCity.value.trim()
        val industry = if (_searchIndustry.value == "All Industries") "" else _searchIndustry.value

        if (skill.isBlank()) {
            _businessSearchError.value = "Please enter a skill to analyze"
            return
        }

        viewModelScope.launch {
            _isSearchingBusinesses.value = true
            _businessSearchError.value = null
            try {
                val results = repository.searchBusinesses(skill, country, city, industry)
                _businessResults.value = results
                if (results.isEmpty()) {
                    _businessSearchError.value = "No businesses found for this combination. Try another city or skill."
                }
            } catch (e: Exception) {
                _businessSearchError.value = e.message ?: "Failed to analyze businesses"
            } finally {
                _isSearchingBusinesses.value = false
            }
        }
    }

    fun toggleSaveBusiness(business: BusinessMatch) {
        viewModelScope.launch {
            val isAlreadySaved = savedBusinessIds.value.contains(business.id)
            if (isAlreadySaved) {
                repository.deleteBusiness(business.id)
            } else {
                repository.saveBusiness(business)
            }
        }
    }

    // --- Hiring & Needs Actions ---
    fun setHiringSkill(skill: String) {
        _hiringSkill.value = skill
    }

    fun setHiringCountry(country: String) {
        _hiringCountry.value = country
    }

    fun setHiringFilter(filter: String) {
        _hiringFilter.value = filter
        searchHiring()
    }

    fun setSelectedHiringPost(post: HiringPost?) {
        _selectedHiringForDetail.value = post
    }

    fun searchHiring() {
        val skill = _hiringSkill.value.trim()
        val country = _hiringCountry.value.trim()
        val filter = _hiringFilter.value

        viewModelScope.launch {
            _isSearchingHiring.value = true
            _hiringSearchError.value = null
            try {
                val results = repository.searchHiringAndNeeds(skill, country, filter)
                _hiringResults.value = results
            } catch (e: Exception) {
                _hiringSearchError.value = e.message ?: "Failed to find hiring posts"
            } finally {
                _isSearchingHiring.value = false
            }
        }
    }

    fun toggleSaveHiringPost(post: HiringPost) {
        viewModelScope.launch {
            val isAlreadySaved = savedHiringPostIds.value.contains(post.id)
            if (isAlreadySaved) {
                repository.deleteHiringPost(post.id)
            } else {
                repository.saveHiringPost(post)
            }
        }
    }

    // --- CRM Actions ---
    fun setCrmTab(tab: String) {
        _crmTab.value = tab
    }

    fun setCrmStatusFilter(status: String) {
        _crmStatusFilter.value = status
    }

    fun updateBusinessStatus(id: String, status: String) {
        viewModelScope.launch {
            repository.updateBusinessStatus(id, status)
        }
    }

    fun updateBusinessNotes(id: String, notes: String) {
        viewModelScope.launch {
            repository.updateBusinessNotes(id, notes)
        }
    }

    fun deleteSavedBusiness(id: String) {
        viewModelScope.launch {
            repository.deleteBusiness(id)
        }
    }

    fun updateHiringPostStatus(id: String, status: String) {
        viewModelScope.launch {
            repository.updateHiringPostStatus(id, status)
        }
    }

    fun updateHiringPostNotes(id: String, notes: String) {
        viewModelScope.launch {
            repository.updateHiringPostNotes(id, notes)
        }
    }

    fun deleteSavedHiringPost(id: String) {
        viewModelScope.launch {
            repository.deleteHiringPost(id)
        }
    }

    // --- Pitch Studio Actions ---
    fun setPitchTone(tone: String) {
        _pitchTone.value = tone
    }

    fun setPitchTarget(name: String, context: String, skill: String, defaultPitch: String = "") {
        _pitchTargetName.value = name
        _pitchTargetContext.value = context
        _pitchSkill.value = skill
        _generatedPitch.value = defaultPitch
    }

    fun updatePitchContent(content: String) {
        _generatedPitch.value = content
    }

    fun generatePitch() {
        val targetName = _pitchTargetName.value.ifBlank { "Business Owner" }
        val targetContext = _pitchTargetContext.value
        val skill = _pitchSkill.value.ifBlank { _searchSkill.value }
        val tone = _pitchTone.value

        viewModelScope.launch {
            _isGeneratingPitch.value = true
            try {
                val pitch = repository.generatePitch(targetName, targetContext, skill, tone)
                _generatedPitch.value = pitch
            } catch (e: Exception) {
                _generatedPitch.value = "Failed to generate pitch: ${e.message}"
            } finally {
                _isGeneratingPitch.value = false
            }
        }
    }
}

class SkillRadarViewModelFactory(
    private val repository: SkillRadarRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SkillRadarViewModel::class.java)) {
            return SkillRadarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
