package com.example.model

enum class FontSupportLevel {
    /**
     * OEM has a direct/accessible Theme Manager or Font Manager engine
     * (e.g., Xiaomi HyperOS/MIUI, Huawei EMUI, Honor MagicOS).
     */
    NATIVE_THEME_ENGINE,

    /**
     * OEM provides a dedicated Font Style / Font Size picker in System Display Settings
     * (e.g., Samsung One UI, Oppo ColorOS, Realme UI, Vivo Funtouch).
     */
    SETTINGS_FONT_PICKER,

    /**
     * Stock Android / Pixel or OEM with locked font engine where system-wide replacement
     * is guarded by Android framework security. Safe alternatives: export font package,
     * display settings styles, or in-app preview and usage.
     */
    RESTRICTED_MANUAL_GUIDE
}

data class GuideStep(
    val stepNumber: Int,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val intentPackage: String? = null,
    val intentAction: String? = null
)

data class DeviceInfo(
    val brand: String,
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val sdkInt: Int,
    val romName: String,
    val supportLevel: FontSupportLevel,
    val summaryAr: String,
    val summaryEn: String,
    val detailedExplanationAr: String,
    val detailedExplanationEn: String,
    val guideSteps: List<GuideStep>,
    val recommendedIntentPackage: String? = null,
    val recommendedIntentAction: String? = null
)
