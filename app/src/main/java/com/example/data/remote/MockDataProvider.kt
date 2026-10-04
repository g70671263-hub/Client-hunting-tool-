package com.example.data.remote

import com.example.data.model.BusinessMatch
import com.example.data.model.HiringPost
import java.util.UUID

object MockDataProvider {

    fun generateCuratedBusinesses(
        skill: String,
        country: String,
        city: String = "",
        industry: String = ""
    ): List<BusinessMatch> {
        val targetCity = if (city.isNotBlank()) city else getPrimaryCityForCountry(country)
        val cleanSkill = skill.ifBlank { "Digital Marketing & Web Services" }

        val archetypes = listOf(
            Triple(
                "Apex Dental & Implant Studio",
                "Healthcare & Dental Clinic",
                "142 Medical Pavilion Blvd, $targetCity"
            ),
            Triple(
                "Sterling Law & Advisory Group",
                "Legal & Corporate Services",
                "88 Financial Plaza, Suite 400, $targetCity"
            ),
            Triple(
                "Bella Vita Trattoria & Wine Bar",
                "Hospitality & Fine Dining",
                "19 Heritage Cobblestone Way, $targetCity"
            ),
            Triple(
                "Vanguard Precision Automotive",
                "Automotive & Fleet Maintenance",
                "310 Industrial Park Rd, $targetCity"
            ),
            Triple(
                "Bloom & Sprout Organic Grocers",
                "Specialty Retail & E-commerce",
                "77 Green Market Square, $targetCity"
            ),
            Triple(
                "Ironclad CrossFit & Performance Lab",
                "Fitness & Wellness Facility",
                "505 Athlete Way, $targetCity"
            ),
            Triple(
                "Skyline Boutique Realty",
                "Real Estate & Property Development",
                "1200 Grand Avenue, 12th Floor, $targetCity"
            )
        )

        val filteredArchetypes = if (industry.isNotBlank()) {
            archetypes.filter { it.second.contains(industry, ignoreCase = true) }.ifEmpty { archetypes }
        } else archetypes

        return filteredArchetypes.mapIndexed { index, (name, category, address) ->
            val matchScore = 96 - (index * 2)
            val mapsQuery = "$name, $targetCity, $country"

            val gap = when {
                cleanSkill.contains("Web", ignoreCase = true) || cleanSkill.contains("Dev", ignoreCase = true) || cleanSkill.contains("Code", ignoreCase = true) ->
                    "Outdated legacy website lacking mobile responsiveness, fast appointment bookings, and proper schema markup, resulting in lost digital leads."
                cleanSkill.contains("SEO", ignoreCase = true) || cleanSkill.contains("Maps", ignoreCase = true) || cleanSkill.contains("Google", ignoreCase = true) ->
                    "Google Maps Business profile is under-optimized with missing category tags, zero structured Q&A, and weak local geo-relevance ranking behind competitors."
                cleanSkill.contains("AI", ignoreCase = true) || cleanSkill.contains("Bot", ignoreCase = true) || cleanSkill.contains("Automation", ignoreCase = true) ->
                    "Staff spends 15+ hours weekly answering repetitive customer inquiries and manually logging leads instead of having an automated 24/7 AI receptionist."
                cleanSkill.contains("Social", ignoreCase = true) || cleanSkill.contains("Video", ignoreCase = true) || cleanSkill.contains("Reel", ignoreCase = true) ->
                    "Inconsistent social media presence with zero short-form video content, missing out on local viral brand discovery and youth demographic engagement."
                cleanSkill.contains("Design", ignoreCase = true) || cleanSkill.contains("Brand", ignoreCase = true) || cleanSkill.contains("UI", ignoreCase = true) ->
                    "Brand identity and digital collateral look fragmented across platforms, hurting perceived premium positioning and conversion rates."
                else ->
                    "Operations rely on outdated manual processes and unoptimized customer touchpoints that can be modernized with $cleanSkill."
            }

            val solution = when {
                cleanSkill.contains("Web", ignoreCase = true) || cleanSkill.contains("Dev", ignoreCase = true) ->
                    "Build a lightning-fast modern web application with integrated calendar booking, SMS confirmations, and automated review collection triggers."
                cleanSkill.contains("SEO", ignoreCase = true) || cleanSkill.contains("Maps", ignoreCase = true) ->
                    "Execute a complete local Maps optimization sprint: audit local citations, optimize GMB attributes, and implement geo-targeted landing pages."
                cleanSkill.contains("AI", ignoreCase = true) || cleanSkill.contains("Bot", ignoreCase = true) || cleanSkill.contains("Automation", ignoreCase = true) ->
                    "Deploy an intelligent customer support and lead qualification AI agent that captures client details and syncs directly into their calendar."
                cleanSkill.contains("Social", ignoreCase = true) || cleanSkill.contains("Video", ignoreCase = true) ->
                    "Produce high-retention short-form video reels highlighting customer transformations and behind-the-scenes craft to drive local footfall."
                else ->
                    "Deliver a bespoke implementation of $cleanSkill tailored specifically to streamline client acquisition and operational efficiency."
            }

            val deliverables = listOf(
                "Comprehensive Baseline Audit & Competitor Gap Analysis",
                "Full Implementation & Testing of $cleanSkill Solution",
                "Automated Reporting & 30-Day Follow-Up Performance Optimization"
            )

            val roi = when (index % 3) {
                0 -> "+30% to +45% increase in verified inbound inquiries within 60 days"
                1 -> "Saves staff 8-12 hours per week in manual back-and-forth communication"
                else -> "Projected $3,500 - $8,000 monthly incremental revenue through improved conversion"
            }

            val pricing = when (index % 4) {
                0 -> "$1,800 - $3,500 Project"
                1 -> "$2,400 Setup + $600/mo Retainer"
                2 -> "$3,200 - $5,000 Turnkey"
                else -> "$1,200 - $2,200 Sprint"
            }

            val pitch = "Hi $name Team, I was analyzing top $category businesses in $targetCity and noticed your Google presence has high demand but could easily capture 30% more clients with an upgraded $cleanSkill workflow. I've prepared a brief 2-minute overview showing the exact gaps—would you be open to seeing it?"

            BusinessMatch(
                id = "mock-biz-$index-${UUID.randomUUID()}",
                name = name,
                category = category,
                city = targetCity,
                country = country,
                address = address,
                googleMapsQuery = mapsQuery,
                matchScore = matchScore,
                currentGap = gap,
                howWeHelp = solution,
                deliverables = deliverables,
                expectedRoi = roi,
                estimatedPricing = pricing,
                outreachPitch = pitch,
                contactChannels = listOf("Google Maps Profile", "Phone", "Website Form", "Direct Email"),
                phone = "+1 (555) ${100 + index * 37}-${2000 + index * 111}",
                website = "https://${name.lowercase().replace(" ", "").replace("&", "")}.example.com",
                isSaved = false
            )
        }
    }

    fun generateCuratedHiringPosts(
        skill: String,
        country: String,
        filter: String = ""
    ): List<HiringPost> {
        val cleanSkill = skill.ifBlank { "Full-Stack Development & AI" }

        val posts = listOf(
            HiringPost(
                id = "hire-post-1-${UUID.randomUUID()}",
                title = "[Urgent] Seeking Specialist for $cleanSkill Overhaul",
                company = "HyperScale Ventures (B2B SaaS)",
                platformSource = "LinkedIn Post",
                postedAgo = "1 hour ago",
                location = "$country (Remote Friendly)",
                country = country,
                budget = "$3,500 - $5,500 Fixed Scope",
                postType = "Urgent Need",
                description = "Our client roster has grown 200% this quarter and we need an experienced expert in $cleanSkill to jump in and streamline our client deliverables. Fast turnaround and direct communication required.",
                requirements = listOf(
                    "Proven hands-on experience in $cleanSkill",
                    "Availability to start within 5-7 business days",
                    "Portfolio or case study demonstrating tangible ROI"
                ),
                howToPitch = "Show a 2-sentence case study of a past project where you delivered $cleanSkill under tight deadlines. Emphasize immediate availability.",
                readyPitchProposal = "Hi Team at HyperScale Ventures, I saw your urgent need for $cleanSkill. I have delivered similar high-impact solutions for scaling B2B teams with 100% on-time delivery. I can start immediately and deliver the initial milestone within 48 hours. Let's do a quick intro call today."
            ),
            HiringPost(
                id = "hire-post-2-${UUID.randomUUID()}",
                title = "Monthly Retainer: $cleanSkill Partner for Agency",
                company = "BlueWave Digital Agency",
                platformSource = "Twitter / X Gig Request",
                postedAgo = "3 hours ago",
                location = "$country / Global Remote",
                country = country,
                budget = "$2,800/month Retainer",
                postType = "Retainer Contract",
                description = "Looking for a reliable freelance partner specializing in $cleanSkill to handle ongoing client accounts (approx 15-20 hours/month). Ongoing long-term collaboration with guaranteed monthly billing.",
                requirements = listOf(
                    "Strong autonomy and proactive problem solving",
                    "Expertise in modern tools and best practices for $cleanSkill",
                    "Fluent written English and timely check-ins"
                ),
                howToPitch = "Propose a pilot sprint at a reduced commitment so they can test working with you risk-free before locking in the retainer.",
                readyPitchProposal = "Hey BlueWave team, I work with digital agencies providing dedicated $cleanSkill support. I can take over client tickets seamlessly with zero hand-holding required. Happy to do a 1-week paid trial to show you my workflow."
            ),
            HiringPost(
                id = "hire-post-3-${UUID.randomUUID()}",
                title = "RFP: Custom Solution & Architecture for $cleanSkill",
                company = "Crestview Health & Wellness",
                platformSource = "Upwork RFP",
                postedAgo = "5 hours ago",
                location = "$country",
                country = country,
                budget = "$4,000 - $7,000 Total",
                postType = "Contract Gig",
                description = "We are modernizing our multi-location clinic operations. We need a consultant to audit our existing infrastructure and implement a state-of-the-art solution utilizing $cleanSkill. Detailed scope document available upon NDA.",
                requirements = listOf(
                    "Experience with health/wellness or multi-location businesses",
                    "Architecture design and security compliance",
                    "Milestone-based delivery model"
                ),
                howToPitch = "Emphasize data security, client confidentiality, and provide an outline of your audit-first methodology.",
                readyPitchProposal = "Hello Crestview Health, your multi-location modernization project aligns directly with my core specialization in $cleanSkill. I have built compliant, scalable systems for regional healthcare practices. I would love to review your scope and provide a structured milestone roadmap."
            ),
            HiringPost(
                id = "hire-post-4-${UUID.randomUUID()}",
                title = "Need Freelancer to fix & optimize our $cleanSkill setup ASAP",
                company = "Apex E-Commerce Brands",
                platformSource = "Reddit /r/forhire",
                postedAgo = "Yesterday",
                location = "$country (Remote)",
                country = country,
                budget = "$65 - $85 / hr",
                postType = "Hourly Project",
                description = "Previous contractor left our setup half-finished. Need an expert who knows $cleanSkill inside out to inspect the setup, fix critical bottlenecks, and hand it over fully documented.",
                requirements = listOf(
                    "Debugging and code/workflow audit proficiency",
                    "Clear asynchronous documentation skills",
                    "Immediate availability for screen-share sync"
                ),
                howToPitch = "Focus on your diagnostic approach: state that you can diagnose the bottleneck within the first 2 hours.",
                readyPitchProposal = "Hey Apex Brands, I specialize in rescuing and optimizing unfinished $cleanSkill deployments. I can jump in, diagnose the exact blocker within a 2-hour audit, and give you an actionable checklist before proceeding with the fixes."
            ),
            HiringPost(
                id = "hire-post-5-${UUID.randomUUID()}",
                title = "Looking for a dedicated $cleanSkill specialist for Q4 launch",
                company = "Starlight Media & Entertainment",
                platformSource = "Direct Job Board",
                postedAgo = "2 days ago",
                location = "$country",
                country = country,
                budget = "$5,000 Flat Fee",
                postType = "Project-Based",
                description = "Gearing up for our flagship product launch this upcoming quarter. We need someone who lives and breathes $cleanSkill to build and deploy key assets to ensure high user conversion and retention.",
                requirements = listOf(
                    "Deep knowledge of high-conversion principles",
                    "Cross-functional collaboration with marketing lead",
                    "Track record of high-traffic releases"
                ),
                howToPitch = "Share your launch playbook or metrics from previous product rollouts you contributed to.",
                readyPitchProposal = "Hi Starlight Media, your Q4 launch sounds exciting! I have helped 8+ brands execute product launches using tailored $cleanSkill strategies, directly increasing conversion rates by an average of 34%. Let's discuss your key launch goals."
            )
        )

        return if (filter.isNotBlank() && filter != "All") {
            posts.filter { it.postType.contains(filter, ignoreCase = true) || it.title.contains(filter, ignoreCase = true) }.ifEmpty { posts }
        } else {
            posts
        }
    }

    fun generateFallbackPitch(targetName: String, skill: String, tone: String): String {
        return when (tone) {
            "Direct Executive ROI" -> """
Subject: Quick question regarding $targetName's customer acquisition & $skill

Hi $targetName Team,

I noticed that while your business has a great local reputation, your current setup is missing out on qualified leads that can be unlocked using $skill.

In similar businesses, implementing this solution has driven a 25-35% increase in verified bookings while saving 10+ staff hours every week.

I have put together a quick 3-bullet breakdown of where you are losing prospects. Would you be opposed to a brief 5-minute review call this week?

Best regards,
Outreach Specialist
""".trimIndent()

            "Free Audit Hook" -> """
Subject: Free audit for $targetName: 3 missed opportunities in $skill

Hi $targetName Team,

I recently analyzed local businesses in your category and compiled a complimentary audit of $targetName. 

I identified 3 specific bottlenecks where potential customers drop off due to gaps in $skill, and mapped out exact fixes.

No sales pitch—just wanted to share the insights. Can I send over the 2-page PDF summary or a short video walk-through?

Warmly,
Consultant
""".trimIndent()

            "Casual & Friendly" -> """
Subject: Loving what you're doing at $targetName! Quick thought

Hey team at $targetName,

Big fan of your work in the local area! I was looking over your digital channels and noticed a quick win you could implement with $skill that would make customer onboarding much smoother.

I've helped a few other folks in your industry tackle this with great results. Happy to share what worked if you're interested!

Cheers,
Freelance Partner
""".trimIndent()

            else -> """
Subject: Strategic proposal for $targetName: Enhancing operations with $skill

Dear $targetName Team,

I am writing to share a high-impact solution tailored specifically for your business operations. By leveraging modern $skill strategies, similar enterprises have achieved measurable efficiency gains and expanded customer reach.

Key benefits include:
- Streamlined automated workflow tailored to your niche
- Measurable 20-30% increase in inbound customer inquiries
- Comprehensive 30-day performance tracking and optimization

I would welcome the opportunity to discuss how this solution aligns with your growth goals. Are you available for a brief conversation this Thursday?

Sincerely,
Growth Advisor
""".trimIndent()
        }
    }

    private fun getPrimaryCityForCountry(country: String): String {
        return when (country.lowercase()) {
            "united states", "usa", "us" -> "New York"
            "united kingdom", "uk" -> "London"
            "canada" -> "Toronto"
            "australia" -> "Sydney"
            "germany" -> "Berlin"
            "france" -> "Paris"
            "united arab emirates", "uae" -> "Dubai"
            "india" -> "Mumbai"
            "singapore" -> "Singapore"
            "japan" -> "Tokyo"
            else -> "Central District"
        }
    }
}
