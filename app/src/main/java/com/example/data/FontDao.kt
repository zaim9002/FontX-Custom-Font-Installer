package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.FontItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FontDao {
    @Query("SELECT * FROM fonts ORDER BY name ASC")
    fun getAllFonts(): Flow<List<FontItem>>

    @Query("SELECT * FROM fonts WHERE isFeatured = 1 ORDER BY downloadCount DESC")
    fun getFeaturedFonts(): Flow<List<FontItem>>

    @Query("SELECT * FROM fonts WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteFonts(): Flow<List<FontItem>>

    @Query("SELECT * FROM fonts WHERE isDownloaded = 1 ORDER BY lastUsedTimestamp DESC, name ASC")
    fun getDownloadedFonts(): Flow<List<FontItem>>

    @Query("SELECT * FROM fonts WHERE isImported = 1 ORDER BY lastUsedTimestamp DESC, name ASC")
    fun getImportedFonts(): Flow<List<FontItem>>

    @Query("SELECT * FROM fonts WHERE id = :fontId")
    fun getFontById(fontId: String): Flow<FontItem?>

    @Query("SELECT * FROM fonts WHERE language = 'Arabic' OR language = 'Multilingual' ORDER BY downloadCount DESC")
    fun getArabicFonts(): Flow<List<FontItem>>

    @Query("SELECT * FROM fonts WHERE language = 'English' OR language = 'Multilingual' ORDER BY downloadCount DESC")
    fun getEnglishFonts(): Flow<List<FontItem>>

    @Query("""
        SELECT * FROM fonts 
        WHERE name LIKE '%' || :query || '%' 
           OR arabicName LIKE '%' || :query || '%' 
           OR tags LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%' 
           OR author LIKE '%' || :query || '%'
        ORDER BY downloadCount DESC
    """)
    fun searchFonts(query: String): Flow<List<FontItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFonts(fonts: List<FontItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFont(font: FontItem)

    @Update
    suspend fun updateFont(font: FontItem)

    @Delete
    suspend fun deleteFont(font: FontItem)

    @Query("UPDATE fonts SET isFavorite = :isFavorite WHERE id = :fontId")
    suspend fun updateFavorite(fontId: String, isFavorite: Boolean)

    @Query("UPDATE fonts SET isDownloaded = :isDownloaded, localFilePath = :filePath WHERE id = :fontId")
    suspend fun updateDownloadStatus(fontId: String, isDownloaded: Boolean, filePath: String?)

    @Query("UPDATE fonts SET lastUsedTimestamp = :timestamp WHERE id = :fontId")
    suspend fun updateLastUsed(fontId: String, timestamp: Long)
}
