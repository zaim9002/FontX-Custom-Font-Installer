package com.example.compat.provider

import android.content.Context
import android.content.Intent
import com.example.model.DeviceInfo
import com.example.model.GuideStep
import java.io.File

interface FontProvider {
    fun getVendorName(): String
    fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean
    fun getCompatibilityInfo(
        context: Context,
        manufacturer: String,
        brand: String,
        model: String,
        androidVersion: String,
        sdkInt: Int,
        romName: String
    ): DeviceInfo

    fun createApplyIntent(context: Context, fontFile: File?): Intent?
    fun createThemeStoreIntent(context: Context): Intent?
    fun exportFontPackage(context: Context, fontFile: File, fontTitle: String, outputDir: File): File?
    fun getInstallationGuide(context: Context): List<GuideStep>
}
