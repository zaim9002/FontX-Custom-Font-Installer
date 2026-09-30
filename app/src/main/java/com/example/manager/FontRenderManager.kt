package com.example.manager

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.ConcurrentHashMap

object FontRenderManager {

    private val fontFamilyCache = ConcurrentHashMap<String, FontFamily>()

    /**
     * Verifies that the file is a genuine TTF or OTF font file by reading magic header bytes.
     */
    fun validateFontFile(file: File): Boolean {
        if (!file.exists() || !file.canRead() || file.length() < 1024 || file.length() > 50 * 1024 * 1024) {
            return false
        }

        return try {
            FileInputStream(file).use { input ->
                val header = ByteArray(4)
                val read = input.read(header)
                if (read < 4) return false

                // TTF: 0x00010000 or 'true' (0x74727565) or 'typ1'
                // OTF: 'OTTO' (0x4F54544F)
                // TTC: 'ttcf' (0x74746366)
                val isTrueType = (header[0] == 0x00.toByte() && header[1] == 0x01.toByte() &&
                        header[2] == 0x00.toByte() && header[3] == 0x00.toByte()) ||
                        (header[0] == 't'.code.toByte() && header[1] == 'r'.code.toByte() &&
                                header[2] == 'u'.code.toByte() && header[3] == 'e'.code.toByte())

                val isOpenType = header[0] == 'O'.code.toByte() && header[1] == 'T'.code.toByte() &&
                        header[2] == 'T'.code.toByte() && header[3] == 'O'.code.toByte()

                val isCollection = header[0] == 't'.code.toByte() && header[1] == 't'.code.toByte() &&
                        header[2] == 'c'.code.toByte() && header[3] == 'f'.code.toByte()

                isTrueType || isOpenType || isCollection
            }
        } catch (e: Throwable) {
            false
        }
    }

    /**
     * Safely obtains or compiles a Jetpack Compose FontFamily from a local font file.
     * Caches both successes and fallbacks so the UI thread is never blocked or stressed.
     */
    fun getFontFamily(localFilePath: String?): FontFamily {
        if (localFilePath.isNullOrBlank()) {
            return FontFamily.Default
        }

        fontFamilyCache[localFilePath]?.let { return it }

        val file = File(localFilePath)
        if (!file.exists() || !file.canRead()) {
            fontFamilyCache[localFilePath] = FontFamily.Default
            return FontFamily.Default
        }

        return try {
            // Passing a single Font(file) is safe across all Android versions
            // Compose automatically handles synthetic bold and italic without font table mismatch
            val singleFont = Font(file = file)
            val family = FontFamily(singleFont)
            fontFamilyCache[localFilePath] = family
            family
        } catch (e: Throwable) {
            fontFamilyCache[localFilePath] = FontFamily.Default
            FontFamily.Default
        }
    }

    /**
     * Preloads a font family in the background so that UI rendering is instant.
     */
    fun preload(localFilePath: String?) {
        if (!localFilePath.isNullOrBlank() && !fontFamilyCache.containsKey(localFilePath)) {
            getFontFamily(localFilePath)
        }
    }

    fun clearCache() {
        fontFamilyCache.clear()
    }
}
