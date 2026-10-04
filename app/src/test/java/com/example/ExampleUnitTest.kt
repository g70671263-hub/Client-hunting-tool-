package com.example

import com.example.data.remote.MockDataProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testGenerateCuratedBusinesses() {
        val businesses = MockDataProvider.generateCuratedBusinesses(
            skill = "Web Development & SEO",
            country = "United States",
            city = "Austin",
            industry = "Healthcare & Dental"
        )

        assertNotNull(businesses)
        assertTrue(businesses.isNotEmpty())
        val first = businesses.first()
        assertEquals("Austin", first.city)
        assertEquals("United States", first.country)
        assertTrue(first.matchScore in 60..99)
        assertTrue(first.howWeHelp.isNotBlank())
        assertTrue(first.currentGap.isNotBlank())
        assertTrue(first.outreachPitch.isNotBlank())
        assertTrue(first.googleMapsQuery.contains("Austin"))
    }

    @Test
    fun testGenerateCuratedHiringPosts() {
        val posts = MockDataProvider.generateCuratedHiringPosts(
            skill = "AI Automation",
            country = "United Kingdom",
            filter = "Urgent Need"
        )

        assertNotNull(posts)
        assertTrue(posts.isNotEmpty())
        val first = posts.first()
        assertTrue(first.title.isNotBlank())
        assertTrue(first.budget.isNotBlank())
        assertTrue(first.readyPitchProposal.isNotBlank())
        assertTrue(first.requirements.isNotEmpty())
    }

    @Test
    fun testGenerateFallbackPitch() {
        val pitch = MockDataProvider.generateFallbackPitch(
            targetName = "Apex Dental",
            skill = "Local SEO",
            tone = "Direct Executive ROI"
        )

        assertTrue(pitch.contains("Apex Dental"))
        assertTrue(pitch.contains("Local SEO"))
        assertTrue(pitch.contains("Subject:"))
    }

    @Test
    fun testCountryCoverageMinimum120() {
        val count = com.example.data.Presets.popularCountries.size
        assertTrue("Expected at least 120 countries, found $count", count >= 120)
    }
}
