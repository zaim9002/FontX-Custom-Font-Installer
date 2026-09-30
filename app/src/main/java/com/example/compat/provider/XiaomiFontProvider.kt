package com.example.compat.provider

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.model.GuideStep
import java.io.File
import java.io.FileOutputStream

class XiaomiFontProvider : FontProvider {
    override fun getVendorName(): String = "Xiaomi / HyperOS / MIUI"

    override fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean {
        return manufacturer.contains("xiaomi", ignoreCase = true) ||
                manufacturer.contains("redmi", ignoreCase = true) ||
                manufacturer.contains("poco", ignoreCase = true) ||
                brand.contains("xiaomi", ignoreCase = true) ||
                brand.contains("redmi", ignoreCase = true) ||
                brand.contains("poco", ignoreCase = true) ||
                romName.contains("MIUI", ignoreCase = true) ||
                romName.contains("HyperOS", ignoreCase = true)
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
            brand = brand.replaceFirstChar { it.uppercase() },
            manufacturer = "Xiaomi",
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = if (romName.isNotBlank()) romName else "HyperOS",
            supportLevel = FontSupportLevel.NATIVE_THEME_ENGINE,
            summaryAr = "مدعوم بالكامل عبر تطبيق الثيمات الرسمي في شاومي (HyperOS / MIUI)",
            summaryEn = "Fully supported via official Xiaomi Theme Manager (HyperOS & MIUI)",
            detailedExplanationAr = "تحتوي واجهات شاومي HyperOS وMIUI على محرك خطوط مدمج ومتطور داخل تطبيق الثيمات الرسمي. يمكنك استيراد الخطوط المخصصة أو تطبيق الخط المصدّر مباشرة عبر مدير الثيمات أو إعدادات الخط.",
            detailedExplanationEn = "Xiaomi HyperOS and MIUI feature an advanced built-in font engine in the official Theme Manager. You can easily import custom TTF fonts or apply them through the official theme & font settings.",
            guideSteps = getInstallationGuide(context),
            recommendedIntentPackage = "com.android.thememanager",
            recommendedIntentAction = "android.intent.action.MAIN"
        )
    }

    override fun createApplyIntent(context: Context, fontFile: File?): Intent {
        // Try MIUI/HyperOS Theme Manager Font activity
        val themeIntent = Intent().apply {
            component = ComponentName("com.android.thememanager", "com.android.thememanager.activity.ThemeTabActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (themeIntent.resolveActivity(context.packageManager) != null) {
            return themeIntent
        }

        // Try direct Theme Manager
        val genericTheme = context.packageManager.getLaunchIntentForPackage("com.android.thememanager")
        if (genericTheme != null) {
            genericTheme.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return genericTheme
        }

        return Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun createThemeStoreIntent(context: Context): Intent? {
        val intent = context.packageManager.getLaunchIntentForPackage("com.android.thememanager")
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
            // HyperOS and MIUI accept .ttf and MTZ packages
            val target = File(outputDir, "HyperOS_${sanitized}.ttf")
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
                titleAr = "تصدير ملف الخط",
                titleEn = "Export Font File",
                descriptionAr = "اضغط على (تصدير الخط) لحفظ ملف TTF المعتمد داخل ذاكرة هاتفك.",
                descriptionEn = "Tap (Export Font) to save the verified TTF font to your device storage."
            ),
            GuideStep(
                stepNumber = 2,
                titleAr = "فتح تطبيق الثيمات (Themes)",
                titleEn = "Open Themes App",
                descriptionAr = "اضغط على (تطبيق الخط) ليقوم التطبيق بفتح تطبيق الثيمات الرسمي في شاومي.",
                descriptionEn = "Tap (Apply Font) to launch the official Xiaomi Themes app.",
                intentPackage = "com.android.thememanager",
                intentAction = "android.intent.action.MAIN"
            ),
            GuideStep(
                stepNumber = 3,
                titleAr = "الدخول لصفحة الخطوط واختيار الخط",
                titleEn = "Go to Fonts & Apply",
                descriptionAr = "انتقل إلى (حسابي / الملف الشخصي) -> (الخطوط / Fonts) -> اختر استيراد أو حدد الخط المصدر واضغط (تعيين).",
                descriptionEn = "Navigate to (Profile / My Account) -> (Fonts) -> Import or choose your exported font and tap (Apply)."
            )
        )
    }
}
