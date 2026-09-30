package com.example.compat.provider

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.model.GuideStep
import java.io.File
import java.io.FileOutputStream

class HuaweiFontProvider : FontProvider {
    override fun getVendorName(): String = "Huawei / EMUI / HarmonyOS"

    override fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean {
        return manufacturer.contains("huawei", ignoreCase = true) ||
                brand.contains("huawei", ignoreCase = true) ||
                romName.contains("EMUI", ignoreCase = true) ||
                romName.contains("HarmonyOS", ignoreCase = true)
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
            brand = "Huawei",
            manufacturer = "Huawei",
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = if (romName.isNotBlank()) romName else "EMUI / HarmonyOS",
            supportLevel = FontSupportLevel.NATIVE_THEME_ENGINE,
            summaryAr = "مدعوم عبر مدير الثيمات الرسمي في هواوي (Huawei Themes / أنماط النص)",
            summaryEn = "Supported via official Huawei Theme Manager (Text Styles)",
            detailedExplanationAr = "تدعم هواتف هواوي وHarmonyOS تغيير خط النظام بالكامل من خلال حزم الخطوط داخل تطبيق (المظاهر / Themes). يجهز التطبيق الخط في صيغة متوافقة ويوجهك لمجلد الثيمات لتطبيقه بنقرة واحدة.",
            detailedExplanationEn = "Huawei EMUI and HarmonyOS devices allow system-wide font switching via the official Themes app under Text Styles. FontX prepares the compatible font file and guides you directly.",
            guideSteps = getInstallationGuide(context),
            recommendedIntentPackage = "com.huawei.android.thememanager",
            recommendedIntentAction = "android.intent.action.MAIN"
        )
    }

    override fun createApplyIntent(context: Context, fontFile: File?): Intent {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.huawei.android.thememanager")
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return launchIntent
        }
        return Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun createThemeStoreIntent(context: Context): Intent? {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.huawei.android.thememanager")
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
            // Huawei font file
            val target = File(outputDir, "Huawei_${sanitized}.ttf")
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
                titleAr = "تصدير الخط المتوافق",
                titleEn = "Export Font File",
                descriptionAr = "اضغط على (تصدير الخط) لحفظ ملف الخط في مجلد (Huawei/Themes) أو التنزيلات.",
                descriptionEn = "Tap (Export Font) to prepare the font file for Huawei Themes."
            ),
            GuideStep(
                stepNumber = 2,
                titleAr = "فتح تطبيق المظاهر (Huawei Themes)",
                titleEn = "Open Huawei Themes",
                descriptionAr = "اضغط على (تطبيق الخط) للانتقال المباشر لتطبيق المظاهر الرسمي.",
                descriptionEn = "Tap (Apply Font) to open the official Huawei Themes application.",
                intentPackage = "com.huawei.android.thememanager",
                intentAction = "android.intent.action.MAIN"
            ),
            GuideStep(
                stepNumber = 3,
                titleAr = "تطبيق أسلوب الخط",
                titleEn = "Apply Text Style",
                descriptionAr = "انتقل إلى (أنا / Me) -> (أنماط النص / Text Styles) واختر الخط ثم اضغط تطبيق.",
                descriptionEn = "Go to (Me) -> (Text Styles / Fonts), select your custom font and tap Apply."
            )
        )
    }
}
