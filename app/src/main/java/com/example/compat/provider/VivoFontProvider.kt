package com.example.compat.provider

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.model.GuideStep
import java.io.File
import java.io.FileOutputStream

class VivoFontProvider : FontProvider {
    override fun getVendorName(): String = "vivo / Funtouch OS / OriginOS"

    override fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean {
        return manufacturer.contains("vivo", ignoreCase = true) ||
                manufacturer.contains("iqoo", ignoreCase = true) ||
                brand.contains("vivo", ignoreCase = true) ||
                brand.contains("iqoo", ignoreCase = true) ||
                romName.contains("Funtouch", ignoreCase = true) ||
                romName.contains("OriginOS", ignoreCase = true)
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
            brand = "vivo",
            manufacturer = "vivo",
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = if (romName.isNotBlank()) romName else "Funtouch OS / OriginOS",
            supportLevel = FontSupportLevel.NATIVE_THEME_ENGINE,
            summaryAr = "مدعوم عبر تطبيق المظاهر الرسمي في فيفو (vivo iTheme / نمط الخط)",
            summaryEn = "Supported via official vivo iTheme app & Font Style",
            detailedExplanationAr = "تدعم أجهزة فيفو وiQOO تخصيص الخطوط بالكامل من خلال تطبيق المظاهر الرسمي (iTheme) أو من خلال الشاشة والسطوع > نمط الخط. يجهز FontX الخط في صيغة معتمدة لتطبيقه بسهولة.",
            detailedExplanationEn = "vivo and iQOO devices feature full font customization via the official iTheme application or Settings > Display & brightness > Font style. FontX exports the verified font package.",
            guideSteps = getInstallationGuide(context),
            recommendedIntentPackage = "com.bbk.theme",
            recommendedIntentAction = "android.intent.action.MAIN"
        )
    }

    override fun createApplyIntent(context: Context, fontFile: File?): Intent {
        val iTheme = context.packageManager.getLaunchIntentForPackage("com.bbk.theme")
        if (iTheme != null) {
            iTheme.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return iTheme
        }
        return Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun createThemeStoreIntent(context: Context): Intent? {
        val iTheme = context.packageManager.getLaunchIntentForPackage("com.bbk.theme")
        iTheme?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return iTheme
    }

    override fun exportFontPackage(
        context: Context,
        fontFile: File,
        fontTitle: String,
        outputDir: File
    ): File? {
        return try {
            val sanitized = fontTitle.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val target = File(outputDir, "vivo_${sanitized}.ttf")
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
                titleAr = "تصدير الخط لفيفو",
                titleEn = "Export Font for vivo",
                descriptionAr = "اضغط على (تصدير الخط) لحفظ الملف بصيغة معتمدة لهواتف vivo.",
                descriptionEn = "Tap (Export Font) to export the font file for vivo."
            ),
            GuideStep(
                stepNumber = 2,
                titleAr = "فتح تطبيق المظاهر (iTheme)",
                titleEn = "Open iTheme Application",
                descriptionAr = "اضغط على (تطبيق الخط) لفتح تطبيق المظاهر الرسمي في فيفو.",
                descriptionEn = "Tap (Apply Font) to open the official vivo iTheme app.",
                intentPackage = "com.bbk.theme",
                intentAction = "android.intent.action.MAIN"
            ),
            GuideStep(
                stepNumber = 3,
                titleAr = "اختيار نمط الخط وتطبيقه",
                titleEn = "Go to Font Style & Apply",
                descriptionAr = "انتقل إلى (الخطوط / Font) واختر الخط المخصص ثم اضغط تطبيق.",
                descriptionEn = "Select your downloaded or custom font under Font style and tap Apply."
            )
        )
    }
}
