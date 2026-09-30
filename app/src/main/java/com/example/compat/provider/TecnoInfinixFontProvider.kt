package com.example.compat.provider

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.model.GuideStep
import java.io.File
import java.io.FileOutputStream

class TecnoInfinixFontProvider : FontProvider {
    override fun getVendorName(): String = "TECNO & Infinix / HiOS & XOS"

    override fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean {
        return manufacturer.contains("tecno", ignoreCase = true) ||
                manufacturer.contains("infinix", ignoreCase = true) ||
                manufacturer.contains("itel", ignoreCase = true) ||
                brand.contains("tecno", ignoreCase = true) ||
                brand.contains("infinix", ignoreCase = true) ||
                brand.contains("itel", ignoreCase = true) ||
                romName.contains("HiOS", ignoreCase = true) ||
                romName.contains("XOS", ignoreCase = true)
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
        val detectedBrand = if (brand.contains("infinix", ignoreCase = true)) "Infinix" else "TECNO"
        val detectedRom = if (romName.isNotBlank()) romName else if (detectedBrand == "Infinix") "XOS" else "HiOS"
        return DeviceInfo(
            brand = detectedBrand,
            manufacturer = manufacturer.replaceFirstChar { it.uppercase() },
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = detectedRom,
            supportLevel = FontSupportLevel.NATIVE_THEME_ENGINE,
            summaryAr = "مدعوم عبر تطبيق الثيمات الرسمي (HiTheme / XTheme / الخطوط)",
            summaryEn = "Supported via official Transsion Theme app (HiTheme / XTheme)",
            detailedExplanationAr = "تحتوي هواتف تكنو وانفينكس على محرك خطوط متكامل داخل تطبيق HiTheme أو XTheme. يدعم التطبيق تثبيت خطوط TTF وتغيير خط النظام بالكامل بكل سهولة وأمان.",
            detailedExplanationEn = "TECNO and Infinix devices feature built-in font switching inside the HiTheme or XTheme app. You can safely apply exported TTF fonts without root.",
            guideSteps = getInstallationGuide(context),
            recommendedIntentAction = Settings.ACTION_DISPLAY_SETTINGS
        )
    }

    override fun createApplyIntent(context: Context, fontFile: File?): Intent {
        val candidates = listOf(
            "com.transsion.magazineservice",
            "com.transsion.xos.launcher",
            "com.shalltry.xlauncher"
        )
        for (pkg in candidates) {
            val intent = context.packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return intent
            }
        }
        return Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun createThemeStoreIntent(context: Context): Intent? {
        val candidates = listOf("com.transsion.magazineservice", "com.transsion.xos.launcher")
        for (pkg in candidates) {
            val intent = context.packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return intent
            }
        }
        return null
    }

    override fun exportFontPackage(
        context: Context,
        fontFile: File,
        fontTitle: String,
        outputDir: File
    ): File? {
        return try {
            val sanitized = fontTitle.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val target = File(outputDir, "Transsion_${sanitized}.ttf")
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
                descriptionAr = "اضغط على (تصدير الخط) لحفظ ملف TTF المعتمد داخل ذاكرة الهاتف.",
                descriptionEn = "Tap (Export Font) to export the font file."
            ),
            GuideStep(
                stepNumber = 2,
                titleAr = "فتح تطبيق HiTheme أو XTheme",
                titleEn = "Open HiTheme or XTheme",
                descriptionAr = "افتح تطبيق الثيمات الرسمي على جهازك أو افتح إعدادات الشاشة.",
                descriptionEn = "Open the HiTheme / XTheme app from your launcher or Display settings.",
                intentAction = Settings.ACTION_DISPLAY_SETTINGS
            ),
            GuideStep(
                stepNumber = 3,
                titleAr = "اختيار الخط وتطبيقه",
                titleEn = "Select Font & Apply",
                descriptionAr = "انتقل إلى قسم (الخطوط / Discovery -> Font) وحدد الخط ثم اضغط تطبيق.",
                descriptionEn = "Go to Fonts section, select your font and tap Apply."
            )
        )
    }
}
