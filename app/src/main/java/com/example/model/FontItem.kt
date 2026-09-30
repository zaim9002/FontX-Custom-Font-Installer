package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fonts")
data class FontItem(
    @PrimaryKey
    val id: String,
    val name: String,
    val arabicName: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val author: String,
    val previewSampleAr: String,
    val previewSampleEn: String,
    val assetFileName: String = "",
    val localFilePath: String? = null,
    val format: String = "TTF",
    val sizeBytes: Long = 0L,
    val language: String = "Multilingual", // "Arabic", "English", "Multilingual"
    val category: String = "Modern", // "Modern", "Classic", "Calligraphy", "Elegant", "Gaming", "iOS Style"
    val tags: String = "",
    val version: String = "1.0",
    val supportedDevices: String = "All Android",
    val isFeatured: Boolean = false,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val isImported: Boolean = false,
    val downloadCount: Int = 1200,
    val lastUsedTimestamp: Long = 0L
)
