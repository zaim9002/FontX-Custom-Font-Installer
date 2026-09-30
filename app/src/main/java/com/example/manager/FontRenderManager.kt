package com.example.manager

import android.content.Context
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtains or compiles a Jetpack Compose FontFamily from a local font file.
     * Falls back safely to FontFamily.Default if file cannot be read.
     */
    fun getFontFamily(localFilePath: String?): FontFamily {
        if (localFilePath.isNullOrBlank()) {
            return FontFamily.Default
        }

        fontFamilyCache[localFilePath]?.let { return it }

        val file = File(localFilePath)
        if (!file.exists() || !file.canRead()) {
            return FontFamily.Default
        }

        return try {
            val normalFont = Font(file = file, weight = FontWeight.Normal, style = FontStyle.Normal)
            val boldFont = Font(file = file, weight = FontWeight.Bold, style = FontStyle.Normal)
            val italicFont = Font(file = file, weight = FontWeight.Normal, style = FontStyle.Italic)
            val family = FontFamily(normalFont, boldFont, italicFont)
            fontFamilyCache[localFilePath] = family
            family
        } catch (e: Throwable) {
            try {
                val singleFont = Font(file = file)
                val family = FontFamily(singleFont)
                fontFamilyCache[localFilePath] = family
                family
            } catch (fallbackEx: Throwable) {
                FontFamily.Default
            }
        }
    }

    fun clearCache() {
        fontFamilyCache.clear()
    }
}
