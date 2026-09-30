package com.example.compat.provider

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.model.GuideStep
import java.io.File
import java.io.FileOutputStream

class RealmeFontProvider : FontProvider {
    override fun getVendorName(): String = "realme / realme UI"

    override fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean {
        return manufacturer.contains("realme", ignoreCase = true) ||
                brand.contains("realme", ignoreCase = true) ||
                romName.contains("realme UI", ignoreCase = true)
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
            brand = "realme",
            manufacturer = "realme",
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = if (romName.isNotBlank()) romName else "realme UI",
            supportLevel = FontSupportLevel.SETTINGS_FONT_PICKER,
            summaryAr = "مدعوم عبر إعدادات التخصيص والخط في واجهة realme UI",
            summaryEn = "Supported via realme UI Personalizations & Font settings",
            detailedExplanationAr = "تتيح واجهة realme UI تغيير خطوط النظام بسلاسة من خلال إعدادات التخصيص أو الشاشة والسطوع > الخط، كما تدعم استيراد الخطوط المخصصة أو استخدام متجر الثيمات.",
            detailedExplanationEn = "realme UI supports system font modifications through Personalizations -> Font & display size, or through the theme manager.",
            guideSteps = getInstallationGuide(context),
            recommendedIntentPackage = "com.heytap.themestore",
            recommendedIntentAction = Settings.ACTION_DISPLAY_SETTINGS
        )
    }

    override fun createApplyIntent(context: Context, fontFile: File?): Intent {
        val themeStore = context.packageManager.getLaunchIntentForPackage("com.heytap.themestore")
        if (themeStore != null) {
            themeStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return themeStore
        }
        return Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun createThemeStoreIntent(context: Context): Intent? {
        val intent = context.packageManager.getLaunchIntentForPackage("com.heytap.themestore")
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
            val target = File(outputDir, "realme_${sanitized}.ttf")
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
                titleAr = "فتح إعدادات التخصيص أو الشاشة",
                titleEn = "Open Personalizations / Display",
                descriptionAr = "اضغط على (تطبيق الخط) للانتقال إلى قسم الخطوط في realme UI.",
                descriptionEn = "Tap (Apply Font) to jump directly into the font settings screen.",
                intentAction = Settings.ACTION_DISPLAY_SETTINGS
            ),
            GuideStep(
                stepNumber = 3,
                titleAr = "تعيين الخط المفضل",
                titleEn = "Select & Apply Font",
                descriptionAr = "اختر الخط من القائمة أو قم بتعيين الخط المستورد واضغط تطبيق.",
                descriptionEn = "Select your font from the list or apply the imported font file."
            )
        )
    }
}
