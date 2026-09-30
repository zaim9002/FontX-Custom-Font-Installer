package com.example.compat.provider

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.model.GuideStep
import java.io.File
import java.io.FileOutputStream

class OppoFontProvider : FontProvider {
    override fun getVendorName(): String = "OPPO / ColorOS"

    override fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean {
        return manufacturer.contains("oppo", ignoreCase = true) ||
                brand.contains("oppo", ignoreCase = true) ||
                romName.contains("ColorOS", ignoreCase = true)
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
            brand = "OPPO",
            manufacturer = "OPPO",
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = if (romName.isNotBlank()) romName else "ColorOS",
            supportLevel = FontSupportLevel.SETTINGS_FONT_PICKER,
            summaryAr = "مدعوم عبر إعدادات الشاشة والسطوع (الخط وحجم العرض) ومتجر HeyTap",
            summaryEn = "Supported via ColorOS Display (Font & display size) & HeyTap Theme Store",
            detailedExplanationAr = "تدعم واجهة ColorOS من OPPO تغيير الخطوط من خلال إعدادات الشاشة والسطوع > الخط وحجم العرض، أو عبر متجر المظاهر الرسمي HeyTap Theme Store.",
            detailedExplanationEn = "OPPO ColorOS natively includes font customization in Settings > Display & Brightness > Font & display size, as well as the HeyTap Theme Store.",
            guideSteps = getInstallationGuide(context),
            recommendedIntentPackage = "com.heytap.themestore",
            recommendedIntentAction = Settings.ACTION_DISPLAY_SETTINGS
        )
    }

    override fun createApplyIntent(context: Context, fontFile: File?): Intent {
        val themeStore = context.packageManager.getLaunchIntentForPackage("com.heytap.themestore")
            ?: context.packageManager.getLaunchIntentForPackage("com.nearme.themestore")
        if (themeStore != null) {
            themeStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return themeStore
        }
        return Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun createThemeStoreIntent(context: Context): Intent? {
        val intent = context.packageManager.getLaunchIntentForPackage("com.heytap.themestore")
            ?: context.packageManager.getLaunchIntentForPackage("com.nearme.themestore")
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    override fun exportFontPackage(
        context: Context,
        fontFile: File,
        fontTitle: String,
        outputDir: File
    ): File? {
        return try {
            val sanitized = fontTitle.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val target = File(outputDir, "ColorOS_${sanitized}.ttf")
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
                titleAr = "تصدير الخط لهاتف OPPO",
                titleEn = "Export Font File",
                descriptionAr = "اضغط على (تصدير الخط) لحفظ ملف TTF المعتمد داخل ذاكرة الهاتف.",
                descriptionEn = "Tap (Export Font) to export the TTF file for ColorOS."
            ),
            GuideStep(
                stepNumber = 2,
                titleAr = "فتح إعدادات الشاشة أو متجر الثيمات",
                titleEn = "Open Display Settings / Theme Store",
                descriptionAr = "اضغط على (تطبيق الخط) لفتح الإعدادات الرسمية مباشرة.",
                descriptionEn = "Tap (Apply Font) to open Display Settings or HeyTap Theme Store.",
                intentAction = Settings.ACTION_DISPLAY_SETTINGS
            ),
            GuideStep(
                stepNumber = 3,
                titleAr = "اختيار الخط وحجم العرض",
                titleEn = "Choose Font & Display Size",
                descriptionAr = "توجه إلى (الشاشة والسطوع) -> (الخط وحجم العرض) وحدد الخط الجديد.",
                descriptionEn = "Go to Display & Brightness -> Font & Display Size to set your font."
            )
        )
    }
}
