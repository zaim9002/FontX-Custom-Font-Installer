package com.example.compat

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.compat.provider.FontProvider
import com.example.compat.provider.GenericAndroidFontProvider
import com.example.compat.provider.HonorFontProvider
import com.example.compat.provider.HuaweiFontProvider
import com.example.compat.provider.OnePlusFontProvider
import com.example.compat.provider.OppoFontProvider
import com.example.compat.provider.RealmeFontProvider
import com.example.compat.provider.SamsungFontProvider
import com.example.compat.provider.TecnoInfinixFontProvider
import com.example.compat.provider.VivoFontProvider
import com.example.compat.provider.XiaomiFontProvider
import com.example.model.DeviceInfo
import java.io.File

object DeviceCompatibilityManager {

    private val providers: List<FontProvider> = listOf(
        SamsungFontProvider(),
        XiaomiFontProvider(),
        HuaweiFontProvider(),
        HonorFontProvider(),
        OppoFontProvider(),
        VivoFontProvider(),
        RealmeFontProvider(),
        OnePlusFontProvider(),
        TecnoInfinixFontProvider(),
        GenericAndroidFontProvider() // fallback must be last
    )

    private fun getSystemProperty(key: String): String {
        return try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getMethod = systemPropertiesClass.getMethod("get", String::class.java)
            val value = getMethod.invoke(null, key) as? String
            value.orEmpty().trim()
        } catch (e: Throwable) {
            ""
        }
    }

    fun detectRomName(): String {
        // One UI
        val oneUiVer = getSystemProperty("ro.build.version.oneui")
        val sepVer = getSystemProperty("ro.build.version.sep")
        if (oneUiVer.isNotBlank()) return "One UI $oneUiVer"
        if (sepVer.isNotBlank()) return "One UI (SEP $sepVer)"

        // Xiaomi / HyperOS / MIUI
        val hyperOs = getSystemProperty("ro.miui.ui.version.name")
        if (hyperOs.contains("HyperOS", ignoreCase = true)) return hyperOs
        if (hyperOs.isNotBlank()) return "MIUI $hyperOs"
        val miuiVer = getSystemProperty("ro.miui.version.code_name")
        if (miuiVer.isNotBlank()) return "MIUI $miuiVer"

        // Huawei EMUI / HarmonyOS
        val emui = getSystemProperty("ro.build.version.emui")
        if (emui.isNotBlank()) return emui
        val harmony = getSystemProperty("hw_sc.build.platform.version")
        if (harmony.isNotBlank()) return "HarmonyOS $harmony"

        // Honor MagicOS
        val magic = getSystemProperty("ro.build.version.magic")
        if (magic.isNotBlank()) return "MagicOS $magic"

        // Oppo ColorOS
        val colorOs = getSystemProperty("ro.build.version.opporom")
        if (colorOs.isNotBlank()) return "ColorOS $colorOs"

        // Realme UI
        val realmeUi = getSystemProperty("ro.build.version.realmeui")
        if (realmeUi.isNotBlank()) return "realme UI $realmeUi"

        // OnePlus OxygenOS
        val oxygenOs = getSystemProperty("ro.oxygen.version")
        if (oxygenOs.isNotBlank()) return "OxygenOS $oxygenOs"

        // Vivo Funtouch / OriginOS
        val vivoDisplay = getSystemProperty("ro.vivo.os.build.display.id")
        if (vivoDisplay.isNotBlank()) return vivoDisplay
        val vivoVer = getSystemProperty("ro.vivo.os.version")
        if (vivoVer.isNotBlank()) return "Funtouch OS $vivoVer"

        // Transsion HiOS / XOS
        val tranos = getSystemProperty("ro.tranos.version")
        if (tranos.isNotBlank()) return tranos
        val transsion = getSystemProperty("ro.transsion.version")
        if (transsion.isNotBlank()) return transsion

        // Motorola
        val moto = getSystemProperty("ro.build.display.id")
        if (Build.MANUFACTURER.contains("motorola", ignoreCase = true) && moto.isNotBlank()) {
            return "Moto MyUX / Hello UI"
        }

        // Google Pixel
        if (Build.BRAND.contains("google", ignoreCase = true) || Build.MANUFACTURER.contains("google", ignoreCase = true)) {
            return "Pixel UI"
        }

        return "Android Stock ROM"
    }

    fun getActiveProvider(manufacturer: String, brand: String, romName: String): FontProvider {
        for (provider in providers) {
            if (provider.isMatchingDevice(manufacturer, brand, romName)) {
                return provider
            }
        }
        return GenericAndroidFontProvider()
    }

    fun getDeviceInfo(context: Context): DeviceInfo {
        val manufacturer = Build.MANUFACTURER.orEmpty()
        val brand = Build.BRAND.orEmpty()
        val model = Build.MODEL.orEmpty()
        val androidVersion = Build.VERSION.RELEASE.orEmpty()
        val sdkInt = Build.VERSION.SDK_INT
        val romName = detectRomName()

        val provider = getActiveProvider(manufacturer, brand, romName)
        return provider.getCompatibilityInfo(
            context = context,
            manufacturer = manufacturer,
            brand = brand,
            model = model,
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            romName = romName
        )
    }

    fun createApplyIntent(context: Context, fontFile: File? = null): Intent {
        val manufacturer = Build.MANUFACTURER.orEmpty()
        val brand = Build.BRAND.orEmpty()
        val romName = detectRomName()
        val provider = getActiveProvider(manufacturer, brand, romName)
        return provider.createApplyIntent(context, fontFile) ?: Intent(Settings.ACTION_DISPLAY_SETTINGS)
    }

    fun createThemeStoreIntent(context: Context): Intent? {
        val manufacturer = Build.MANUFACTURER.orEmpty()
        val brand = Build.BRAND.orEmpty()
        val romName = detectRomName()
        val provider = getActiveProvider(manufacturer, brand, romName)
        return provider.createThemeStoreIntent(context)
    }

    fun exportFont(context: Context, fontFile: File, fontTitle: String): File? {
        val manufacturer = Build.MANUFACTURER.orEmpty()
        val brand = Build.BRAND.orEmpty()
        val romName = detectRomName()
        val provider = getActiveProvider(manufacturer, brand, romName)
        val exportDir = File(context.getExternalFilesDir(null) ?: context.filesDir, "ExportedFonts")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        return provider.exportFontPackage(context, fontFile, fontTitle, exportDir)
    }
}
