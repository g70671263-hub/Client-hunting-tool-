package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.BusinessMatch
import com.example.data.model.HiringPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() {
            val key = try {
                BuildConfig.GEMINI_API_KEY
            } catch (e: Exception) {
                ""
            }
            return if (key == "MY_GEMINI_API_KEY") "" else key
        }

    val isApiKeyConfigured: Boolean
        get() = apiKey.isNotBlank()

    suspend fun searchAndAnalyzeBusinesses(
        skill: String,
        country: String,
        city: String = "",
        industry: String = ""
    ): List<BusinessMatch> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            Log.d("GeminiService", "API key not configured, returning curated smart simulation")
            return@withContext MockDataProvider.generateCuratedBusinesses(skill, country, city, industry)
        }

        val locationScope = if (city.isNotBlank()) "$city, $country" else country
        val industryFilter = if (industry.isNotBlank()) " specializing in or including $industry" else ""

        val prompt = """
You are an expert B2B Growth Consultant and Google Maps Business Intelligence Analyst.
Analyze businesses located in $locationScope$industryFilter that could greatly benefit from hiring someone with the following SKILL:
Skill: "$skill"

Search through representative real-world local business types, establishments, and enterprises found on Google Maps in $locationScope.
For each business, analyze their specific operational gap, how someone with "$skill" can solve it, concrete deliverables, expected ROI, estimated project price, and a ready cold outreach pitch hook.

Return a STRICT JSON ARRAY of 5 to 7 business matches. Do NOT include markdown code fences or explanatory text, only the raw JSON array.
Each object in the array MUST have the following structure:
[
  {
    "name": "Name of Business",
    "category": "Industry / Niche (e.g. Specialty Dental Clinic, Boutique Law Firm, Artisan Bakery)",
    "city": "City Name",
    "country": "$country",
    "address": "Realistic Street Address or District in $locationScope",
    "googleMapsQuery": "Search query to find them on Google Maps (e.g., Name City Country)",
    "matchScore": 92,
    "currentGap": "Detailed breakdown of their current operational or technological deficiency",
    "howWeHelp": "Specific high-impact solution that the skill '$skill' directly delivers",
    "deliverables": ["Deliverable 1", "Deliverable 2", "Deliverable 3"],
    "expectedRoi": "Expected quantifiable business ROI (e.g., +25% lead capture, 8 hrs/week saved)",
    "estimatedPricing": "Estimated project or retainer price (e.g., $1,500 - $3,200)",
    "outreachPitch": "A punchy, high-converting 3-sentence cold pitch customized for this business owner",
    "contactChannels": ["Google Business Profile", "Website Form", "Phone", "Email"],
    "phone": "+1-555-0192",
    "website": "example.com"
  }
]
""".trimIndent()

        try {
            val responseText = callGeminiApi(prompt)
            val cleanedJson = cleanJsonString(responseText)
            val jsonArray = JSONArray(cleanedJson)
            val list = mutableListOf<BusinessMatch>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val deliverablesArray = obj.optJSONArray("deliverables")
                val deliverables = mutableListOf<String>()
                if (deliverablesArray != null) {
                    for (j in 0 until deliverablesArray.length()) {
                        deliverables.add(deliverablesArray.getString(j))
                    }
                }
                if (deliverables.isEmpty()) {
                    deliverables.add("Custom Strategy & Audit")
                    deliverables.add("End-to-End Implementation")
                }

                val channelsArray = obj.optJSONArray("contactChannels")
                val channels = mutableListOf<String>()
                if (channelsArray != null) {
                    for (j in 0 until channelsArray.length()) {
                        channels.add(channelsArray.getString(j))
                    }
                }
                if (channels.isEmpty()) {
                    channels.addAll(listOf("Google Maps Profile", "Email", "Phone"))
                }

                val name = obj.optString("name", "Local Enterprise")
                val bCity = obj.optString("city", city.ifBlank { "Metropolis" })
                val bCountry = obj.optString("country", country)
                val defaultMapsQuery = "$name $bCity $bCountry"

                list.add(
                    BusinessMatch(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        category = obj.optString("category", "Commercial Business"),
                        city = bCity,
                        country = bCountry,
                        address = obj.optString("address", "$bCity Central District"),
                        googleMapsQuery = obj.optString("googleMapsQuery", defaultMapsQuery),
                        matchScore = obj.optInt("matchScore", 90).coerceIn(60, 99),
                        currentGap = obj.optString("currentGap", "Lacks modernized systems aligned with $skill"),
                        howWeHelp = obj.optString("howWeHelp", "Implement tailored solutions leveraging $skill to boost revenue"),
                        deliverables = deliverables,
                        expectedRoi = obj.optString("expectedRoi", "+20-30% operational efficiency"),
                        estimatedPricing = obj.optString("estimatedPricing", "$1,000 - $3,000"),
                        outreachPitch = obj.optString("outreachPitch", "Hi there, I noticed an opportunity to improve your operations using $skill. Would you be open to a quick 5-minute chat?"),
                        contactChannels = channels,
                        phone = obj.optString("phone", ""),
                        website = obj.optString("website", ""),
                        isSaved = false
                    )
                )
            }
            if (list.isNotEmpty()) list else MockDataProvider.generateCuratedBusinesses(skill, country, city, industry)
        } catch (e: Exception) {
            Log.e("GeminiService", "Failed to fetch Gemini businesses: ${e.message}", e)
            MockDataProvider.generateCuratedBusinesses(skill, country, city, industry)
        }
    }

    suspend fun searchHiringAndNeeds(
        skill: String,
        country: String,
        filter: String = ""
    ): List<HiringPost> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            Log.d("GeminiService", "API key not configured, returning curated smart hiring posts")
            return@withContext MockDataProvider.generateCuratedHiringPosts(skill, country, filter)
        }

        val prompt = """
You are a real-time freelance and client opportunity radar.
Find or generate 5 to 7 high-intent, realistic hiring posts, project RFPs, freelance contract gigs, and urgent business needs for someone with the following SKILL:
Skill: "$skill"
Target Country / Location: "$country" (also include top Remote openings accessible from $country)
Filter/Focus: "${filter.ifBlank { "All Opportunities" }}"

Search across realistic job posts, client RFPs, LinkedIn posts, Twitter/X freelance requests, Upwork/freelancer client briefs, and business community postings.
Return a STRICT JSON ARRAY of 5 to 7 objects. Do NOT include markdown code fences or explanatory text, only the raw JSON array.
Structure of each item:
[
  {
    "title": "Clear headline of the post (e.g. [Urgent] Needed: Senior Next.js Developer for E-commerce Storefront)",
    "company": "Company or Client Name",
    "platformSource": "Source (e.g. LinkedIn, Twitter/X, Upwork RFP, Reddit /r/forhire, Direct Job Board)",
    "postedAgo": "e.g. 2 hours ago / Yesterday / 4 hours ago",
    "location": "Location (e.g. London, UK (Remote) or New York, NY)",
    "country": "$country",
    "budget": "Compensation/Budget (e.g. $2,500 - $4,000 Fixed, $50-$70/hr, $3,500/mo Retainer)",
    "postType": "Contract Gig / Urgent Need / Retainer / Full-time Project",
    "description": "Realistic full text of the client's post describing their pain point, what they need done, and timeline",
    "requirements": ["Key Requirement 1", "Key Requirement 2", "Key Requirement 3"],
    "howToPitch": "Tactical advice on the best pitch angle to stand out to this specific client",
    "readyPitchProposal": "A polished, ready-to-send proposal letter tailored to this specific post"
  }
]
""".trimIndent()

        try {
            val responseText = callGeminiApi(prompt)
            val cleanedJson = cleanJsonString(responseText)
            val jsonArray = JSONArray(cleanedJson)
            val list = mutableListOf<HiringPost>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val reqArray = obj.optJSONArray("requirements")
                val requirements = mutableListOf<String>()
                if (reqArray != null) {
                    for (j in 0 until reqArray.length()) {
                        requirements.add(reqArray.getString(j))
                    }
                }
                if (requirements.isEmpty()) {
                    requirements.add("Proven track record in $skill")
                    requirements.add("Fast turnaround and clear communication")
                }

                list.add(
                    HiringPost(
                        id = UUID.randomUUID().toString(),
                        title = obj.optString("title", "Project Need for $skill"),
                        company = obj.optString("company", "Fast-growing Business"),
                        platformSource = obj.optString("platformSource", "LinkedIn Post"),
                        postedAgo = obj.optString("postedAgo", "Just now"),
                        location = obj.optString("location", country),
                        country = country,
                        budget = obj.optString("budget", "$1,500 - $3,000"),
                        postType = obj.optString("postType", "Contract Gig"),
                        description = obj.optString("description", "Looking for a dedicated professional skilled in $skill to assist with urgent project deliverables."),
                        requirements = requirements,
                        howToPitch = obj.optString("howToPitch", "Highlight your past relevant deliverables and offer a quick turnaround."),
                        readyPitchProposal = obj.optString("readyPitchProposal", "Hi, I saw your post regarding $skill and have direct experience solving this exact challenge. Let's connect!"),
                        isSaved = false
                    )
                )
            }
            if (list.isNotEmpty()) list else MockDataProvider.generateCuratedHiringPosts(skill, country, filter)
        } catch (e: Exception) {
            Log.e("GeminiService", "Failed to fetch Gemini hiring posts: ${e.message}", e)
            MockDataProvider.generateCuratedHiringPosts(skill, country, filter)
        }
    }

    suspend fun generateCustomPitch(
        targetName: String,
        targetDetails: String,
        skill: String,
        tone: String
    ): String = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext MockDataProvider.generateFallbackPitch(targetName, skill, tone)
        }

        val prompt = """
You are a master of B2B cold outreach and client acquisition.
Write a high-converting, personalized outreach pitch for:
Target: "$targetName"
Target Details/Context: "$targetDetails"
My Skill to Offer: "$skill"
Tone: "$tone" (e.g. Consultative & Helpful, Direct Executive ROI, Free Audit Hook, Casual & Friendly)

Rules:
1. Include an attention-grabbing Subject Line: "Subject: ..."
2. Focus on solving their immediate problem and showing quantifiable ROI
3. Include a low-friction call-to-action (e.g. a 5-minute loom review or brief call)
4. Keep it concise, professional, and authentic (under 180 words)
""".trimIndent()

        try {
            val response = callGeminiApi(prompt)
            response.trim()
        } catch (e: Exception) {
            Log.e("GeminiService", "Failed to generate custom pitch: ${e.message}", e)
            MockDataProvider.generateFallbackPitch(targetName, skill, tone)
        }
    }

    private suspend fun callGeminiApi(prompt: String): String = withContext(Dispatchers.IO) {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", prompt)
                        }
                        put(partObj)
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)

            val generationConfig = JSONObject().apply {
                put("temperature", 0.4)
                put("topP", 0.9)
            }
            put("generationConfig", generationConfig)
        }

        val body = requestJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            val errBody = response.body?.string() ?: ""
            throw IllegalStateException("Gemini API error ${response.code}: $errBody")
        }

        val responseString = response.body?.string() ?: ""
        val jsonRoot = JSONObject(responseString)
        val candidates = jsonRoot.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            if (parts != null && parts.length() > 0) {
                return@withContext parts.getJSONObject(0).optString("text", "")
            }
        }
        throw IllegalStateException("Empty response from Gemini")
    }

    private fun cleanJsonString(raw: String): String {
        var trimmed = raw.trim()
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.removePrefix("```json").trim()
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.removePrefix("```").trim()
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.removeSuffix("```").trim()
        }
        return trimmed.trim()
    }
}
