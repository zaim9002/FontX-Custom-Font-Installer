package com.example.compat.provider

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.model.GuideStep
import java.io.File
import java.io.FileOutputStream

class HonorFontProvider : FontProvider {
    override fun getVendorName(): String = "HONOR / MagicOS"

    override fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean {
        return manufacturer.contains("honor", ignoreCase = true) ||
                brand.contains("honor", ignoreCase = true) ||
                romName.contains("MagicOS", ignoreCase = true) ||
                romName.contains("Magic UI", ignoreCase = true)
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
            brand = "HONOR",
            manufacturer = "HONOR",
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = if (romName.isNotBlank()) romName else "MagicOS",
            supportLevel = FontSupportLevel.NATIVE_THEME_ENGINE,
            summaryAr = "مدعوم عبر تطبيق الثيمات الرسمي في هونر (HONOR Themes / MagicOS)",
            summaryEn = "Supported via official HONOR Theme Manager on MagicOS",
            detailedExplanationAr = "تدعم هواتف HONOR العاملة بنظام MagicOS تخصيص الخطوط بالكامل عبر تطبيق المظاهر الرسمي في قسم الخطوط أو عبر إعدادات الشاشة وحجم النص.",
            detailedExplanationEn = "HONOR smartphones running MagicOS support font customization directly through the official HONOR Themes app or Display text style settings.",
            guideSteps = getInstallationGuide(context),
            recommendedIntentPackage = "com.hihonor.android.thememanager",
            recommendedIntentAction = "android.intent.action.MAIN"
        )
    }

    override fun createApplyIntent(context: Context, fontFile: File?): Intent {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.hihonor.android.thememanager")
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return launchIntent
        }
        return Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun createThemeStoreIntent(context: Context): Intent? {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.hihonor.android.thememanager")
        launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return launchIntent
    }

    override fun exportFontPackage(
        context: Context,
        fontFile: File,
        fontTitle: String,
        outputDir: File
    ): File? {
        return try {
            val sanitized = fontTitle.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val target = File(outputDir, "Honor_${sanitized}.ttf")
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
                titleAr = "تصدير الخط لهونر",
                titleEn = "Export Font for HONOR",
                descriptionAr = "اضغط على (تصدير الخط) لحفظ الملف بصيغة TTF المعتمدة لهواتف HONOR.",
                descriptionEn = "Tap (Export Font) to export the font file for HONOR devices."
            ),
            GuideStep(
                stepNumber = 2,
                titleAr = "فتح تطبيق المظاهر (HONOR Themes)",
                titleEn = "Open HONOR Themes",
                descriptionAr = "اضغط على (تطبيق الخط) لفتح تطبيق مظاهر هونر الرسمي.",
                descriptionEn = "Tap (Apply Font) to open the HONOR Themes app directly.",
                intentPackage = "com.hihonor.android.thememanager",
                intentAction = "android.intent.action.MAIN"
            ),
            GuideStep(
                stepNumber = 3,
                titleAr = "تحديد الخط وتطبيقه",
                titleEn = "Select Font & Apply",
                descriptionAr = "انتقل إلى (الملف الشخصي / أنا) -> (الخطوط) ثم اختر الخط واضغط تطبيق.",
                descriptionEn = "Go to (Me) -> (Fonts), pick your exported font, and apply."
            )
        )
    }
}
