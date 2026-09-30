package com.example.compat.provider

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.model.GuideStep
import java.io.File
import java.io.FileOutputStream

class SamsungFontProvider : FontProvider {
    override fun getVendorName(): String = "Samsung"

    override fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean {
        return manufacturer.contains("samsung", ignoreCase = true) ||
                brand.contains("samsung", ignoreCase = true) ||
                romName.contains("One UI", ignoreCase = true)
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
        val steps = getInstallationGuide(context)
        return DeviceInfo(
            brand = brand.replaceFirstChar { it.uppercase() },
            manufacturer = "Samsung",
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = if (romName.isNotBlank()) romName else "One UI",
            supportLevel = FontSupportLevel.SETTINGS_FONT_PICKER,
            summaryAr = "مدعوم عبر إعدادات الشاشة وحجم ونمط الخط الرسمية في سامسونج",
            summaryEn = "Supported via official Samsung Display & Font Style Settings",
            detailedExplanationAr = "تدعم أجهزة سامسونج One UI تخصيص خطوط النظام من خلال قسم (حجم الخط ونمطه) داخل إعدادات الشاشة، أو عبر Galaxy Themes. يوفر تطبيق FontX تصدير الخط وفتح شاشة التثبيت فورًا دون أي حاجة للروت.",
            detailedExplanationEn = "Samsung One UI devices officially support font customization through Display > Font size and style, or via Galaxy Themes. FontX exports the font file and navigates you directly to the official picker safely.",
            guideSteps = steps,
            recommendedIntentPackage = "com.android.settings",
            recommendedIntentAction = Settings.ACTION_DISPLAY_SETTINGS
        )
    }

    override fun createApplyIntent(context: Context, fontFile: File?): Intent {
        // Try specific Samsung Font settings intent first, fallback to Display settings
        val intents = listOf(
            Intent().setComponent(
                ComponentName("com.android.settings", "com.android.settings.Settings\$FontPreviewActivity")
            ),
            Intent().setComponent(
                ComponentName("com.android.settings", "com.android.settings.Settings\$FontSizeActivity")
            ),
            Intent(Settings.ACTION_DISPLAY_SETTINGS)
        )

        for (intent in intents) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(context.packageManager) != null) {
                return intent
            }
        }
        return Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun createThemeStoreIntent(context: Context): Intent? {
        val intent = Intent("com.samsung.android.themestore.action.THEME_STORE")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return if (intent.resolveActivity(context.packageManager) != null) intent else null
    }

    override fun exportFontPackage(
        context: Context,
        fontFile: File,
        fontTitle: String,
        outputDir: File
    ): File? {
        return try {
            val sanitized = fontTitle.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val target = File(outputDir, "Samsung_$sanitized.ttf")
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
                descriptionAr = "اضغط على زر (تصدير الخط) لحفظ ملف الخط في مجلد التنزيلات بجهازك.",
                descriptionEn = "Tap (Export Font) to save the verified font file to your device Downloads folder."
            ),
            GuideStep(
                stepNumber = 2,
                titleAr = "فتح إعدادات الشاشة في سامسونج",
                titleEn = "Open Samsung Display Settings",
                descriptionAr = "اضغط على (تطبيق الخط) ليقوم FontX بفتح إعدادات الشاشة مباشرة.",
                descriptionEn = "Tap (Apply Font) and FontX will navigate you straight into Display Settings.",
                intentPackage = "com.android.settings",
                intentAction = Settings.ACTION_DISPLAY_SETTINGS
            ),
            GuideStep(
                stepNumber = 3,
                titleAr = "اختيار حجم الخط ونمطه",
                titleEn = "Select Font Size and Style",
                descriptionAr = "اختر (حجم الخط ونمطه) ثم (أسلوب الخط) واختر الخط المصدر أو نزله من Galaxy Store.",
                descriptionEn = "Go to 'Font size and style' -> 'Font style' to select your downloaded style or install via Galaxy Themes."
            )
        )
    }
}
