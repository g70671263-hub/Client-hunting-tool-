package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillRadarDao {

    // --- Saved Businesses ---
    @Query("SELECT * FROM saved_businesses ORDER BY savedAt DESC")
    fun getAllSavedBusinesses(): Flow<List<SavedBusinessEntity>>

    @Query("SELECT * FROM saved_businesses WHERE id = :id LIMIT 1")
    suspend fun getBusinessById(id: String): SavedBusinessEntity?

    @Query("SELECT id FROM saved_businesses")
    fun getAllSavedBusinessIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: SavedBusinessEntity)

    @Update
    suspend fun updateBusiness(business: SavedBusinessEntity)

    @Query("UPDATE saved_businesses SET status = :status WHERE id = :id")
    suspend fun updateBusinessStatus(id: String, status: String)

    @Query("UPDATE saved_businesses SET notes = :notes WHERE id = :id")
    suspend fun updateBusinessNotes(id: String, notes: String)

    @Query("DELETE FROM saved_businesses WHERE id = :id")
    suspend fun deleteBusinessById(id: String)

    // --- Saved Hiring Posts ---
    @Query("SELECT * FROM saved_hiring_posts ORDER BY savedAt DESC")
    fun getAllSavedHiringPosts(): Flow<List<SavedHiringPostEntity>>

    @Query("SELECT * FROM saved_hiring_posts WHERE id = :id LIMIT 1")
    suspend fun getHiringPostById(id: String): SavedHiringPostEntity?

    @Query("SELECT id FROM saved_hiring_posts")
    fun getAllSavedHiringPostIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHiringPost(post: SavedHiringPostEntity)

    @Update
    suspend fun updateHiringPost(post: SavedHiringPostEntity)

    @Query("UPDATE saved_hiring_posts SET status = :status WHERE id = :id")
    suspend fun updateHiringPostStatus(id: String, status: String)

    @Query("UPDATE saved_hiring_posts SET notes = :notes WHERE id = :id")
    suspend fun updateHiringPostNotes(id: String, notes: String)

    @Query("DELETE FROM saved_hiring_posts WHERE id = :id")
    suspend fun deleteHiringPostById(id: String)
}
