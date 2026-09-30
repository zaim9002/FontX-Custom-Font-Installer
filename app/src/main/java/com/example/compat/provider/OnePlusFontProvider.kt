package com.example.compat.provider

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.model.GuideStep
import java.io.File
import java.io.FileOutputStream

class OnePlusFontProvider : FontProvider {
    override fun getVendorName(): String = "OnePlus / OxygenOS"

    override fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean {
        return manufacturer.contains("oneplus", ignoreCase = true) ||
                brand.contains("oneplus", ignoreCase = true) ||
                romName.contains("OxygenOS", ignoreCase = true)
    }

    override fun getCompatibilityInfo(
        context: Context,
        manufacturer: String,
        brand: String,
        model: String,
        androidVersion: String,
        sdkInt: Int,
        romName: String
    ): DeviceInfo {
        return DeviceInfo(
            brand = "OnePlus",
            manufacturer = "OnePlus",
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = if (romName.isNotBlank()) romName else "OxygenOS",
            supportLevel = FontSupportLevel.SETTINGS_FONT_PICKER,
            summaryAr = "مدعوم عبر إعدادات التخصيص والخط (OxygenOS / OnePlus Sans)",
            summaryEn = "Supported via OxygenOS Wallpapers & style / Font settings",
            detailedExplanationAr = "تدعم أجهزة ون بلس OxygenOS اختيار الخطوط المتوافقة من قسم (الخلفيات والأناقة / التخصيصات) داخل الإعدادات.",
            detailedExplanationEn = "OnePlus devices running OxygenOS officially allow font customization under Wallpapers & style -> Font, supporting custom styles alongside OnePlus Sans and Roboto.",
            guideSteps = getInstallationGuide(context),
            recommendedIntentAction = Settings.ACTION_DISPLAY_SETTINGS
        )
    }

    override fun createApplyIntent(context: Context, fontFile: File?): Intent {
        return Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun createThemeStoreIntent(context: Context): Intent? {
        val themeStore = context.packageManager.getLaunchIntentForPackage("com.heytap.themestore")
        themeStore?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return themeStore
    }

    override fun exportFontPackage(
        context: Context,
        fontFile: File,
        fontTitle: String,
        outputDir: File
    ): File? {
        return try {
            val sanitized = fontTitle.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val target = File(outputDir, "OnePlus_${sanitized}.ttf")
            fontFile.inputStream().use { input ->
                FileOutputStream(target).use { output ->
                    input.copyTo(output)
                }
            }
            target
        } catch (e: Exception) {
            null
        }
    }

    override fun getInstallationGuide(context: Context): List<GuideStep> {
        return listOf(
            GuideStep(
                stepNumber = 1,
                titleAr = "تصدير الخط",
                titleEn = "Export Font File",
                descriptionAr = "اضغط على (تصدير الخط) لحفظ ملف TTF المعتمد.",
                descriptionEn = "Tap (Export Font) to save the font locally."
            ),
            GuideStep(
                stepNumber = 2,
                titleAr = "فتح إعدادات الشاشة والتخصيص",
                titleEn = "Open Display / Wallpapers & style",
                descriptionAr = "اضغط على (تطبيق الخط) لفتح الإعدادات الرسمية في OxygenOS.",
                descriptionEn = "Tap (Apply Font) to open OxygenOS Settings directly.",
                intentAction = Settings.ACTION_DISPLAY_SETTINGS
            ),
            GuideStep(
                stepNumber = 3,
                titleAr = "اختيار الخط",
                titleEn = "Select Font Style",
                descriptionAr = "اختر (الخط وحجم العرض) وحدد الخط المفضل لديك.",
                descriptionEn = "Go to Font & display size, select the desired font and apply."
            )
        )
    }
}
