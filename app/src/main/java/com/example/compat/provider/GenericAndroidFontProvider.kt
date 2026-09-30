package com.example.compat.provider

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.model.GuideStep
import java.io.File
import java.io.FileOutputStream

class GenericAndroidFontProvider : FontProvider {
    override fun getVendorName(): String = "Google Pixel / Motorola / Stock Android"

    override fun isMatchingDevice(manufacturer: String, brand: String, romName: String): Boolean {
        // Fallback for all other devices
        return true
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
        val isPixel = brand.contains("google", ignoreCase = true) || manufacturer.contains("google", ignoreCase = true)
        val isMotorola = brand.contains("motorola", ignoreCase = true) || manufacturer.contains("moto", ignoreCase = true)

        val vendorLabel = when {
            isPixel -> "Google Pixel"
            isMotorola -> "Motorola / Moto"
            manufacturer.isNotBlank() -> manufacturer.replaceFirstChar { it.uppercase() }
            else -> "Android Standard"
        }

        val romLabel = when {
            isPixel -> "Pixel UI / Android Stock"
            isMotorola -> "MyUX / Hello UI"
            romName.isNotBlank() -> romName
            else -> "AOSP / Stock ROM"
        }

        val summaryAr = if (isMotorola) {
            "مدعوم عبر إعدادات التخصيص (Moto Personalize / Fonts)"
        } else {
            "مقيد بنظام حماية Android الرسمي (يتطلب دعم إعدادات النمط أو تصدير الخط)"
        }

        val summaryEn = if (isMotorola) {
            "Supported via Motorola Personalize app & Font Styles"
        } else {
            "Restricted by standard Android security (Use Styles settings or Export)"
        }

        val explanationAr = if (isMotorola) {
            "تحتوي هواتف موتورولا على تطبيق Moto أو إعدادات التخصيص حيث يمكنك تبديل نمط الخط الرسمي. يوفر FontX تصدير الخط وإرشادات التثبيت الآمنة."
        } else {
            "تفرض جوجل في نظام Android الخام والـ Pixel حظرًا أمنيًا على تعديل خطوط النظام بدون صلاحيات الروت أو تحديث رسمي. يلتزم FontX بالأمان التام ويوفر لك: فتح إعدادات الأنماط الرسمية، تصدير ملفات الخط لاستخدامها في التطبيقات الداعمة، وتطبيق الخط فوريًا داخل FontX."
        }

        val explanationEn = if (isMotorola) {
            "Motorola devices feature the Moto app or Personalize settings where you can change the font style. FontX exports the verified font and navigates you to Display settings."
        } else {
            "Standard Android (and Google Pixel) intentionally restricts changing system-wide fonts without root or custom firmware for security reasons. FontX maintains 100% device safety: you can export fonts, use them in supported third-party apps, try system Style settings, and enjoy custom fonts inside FontX."
        }

        val supportLevel = if (isMotorola) {
            FontSupportLevel.SETTINGS_FONT_PICKER
        } else {
            FontSupportLevel.RESTRICTED_MANUAL_GUIDE
        }

        return DeviceInfo(
            brand = vendorLabel,
            manufacturer = manufacturer.replaceFirstChar { it.uppercase() },
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = romLabel,
            supportLevel = supportLevel,
            summaryAr = summaryAr,
            summaryEn = summaryEn,
            detailedExplanationAr = explanationAr,
            detailedExplanationEn = explanationEn,
            guideSteps = getInstallationGuide(context),
            recommendedIntentAction = Settings.ACTION_DISPLAY_SETTINGS
        )
    }

    override fun createApplyIntent(context: Context, fontFile: File?): Intent {
        return Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun createThemeStoreIntent(context: Context): Intent? {
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
            val target = File(outputDir, "${sanitized}.ttf")
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
                titleAr = "فحص قيود أندرويد الرسمية",
                titleEn = "Android Policy Review",
                descriptionAr = "نظام Android الخام لا يتيح لأي تطبيق تعديل خط النظام مباشرة للحفاظ على استقرار وأمان الهاتف.",
                descriptionEn = "Standard Android restricts third-party apps from altering system typography for security."
            ),
            GuideStep(
                stepNumber = 2,
                titleAr = "فتح إعدادات الأنماط والألوان",
                titleEn = "Open Wallpaper & Style",
                descriptionAr = "افتح إعدادات الشاشة والخلفية لتجربة الأنماط والخطوط المدمجة التي يتيحها المصنع.",
                descriptionEn = "Open Display / Wallpaper & Style to select any vendor-provided font options.",
                intentAction = Settings.ACTION_DISPLAY_SETTINGS
            ),
            GuideStep(
                stepNumber = 3,
                titleAr = "تصدير الخط واستخدامه في التطبيقات الداعمة",
                titleEn = "Export & Use in Compatible Apps",
                descriptionAr = "استخدم زر (تصدير الخط) لحفظ ملف TTF واستخدامه في برامج التصميم وقارئات الكتب واللانشرات الداعمة للخطوط.",
                descriptionEn = "Tap Export to save the font file for use in launchers, editors, and reader apps."
            )
        )
    }
}
