package com.example.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.compat.DeviceCompatibilityManager
import com.example.manager.FontRenderManager
import com.example.model.FontItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class FontRepository(
    private val context: Context,
    private val fontDao: FontDao
) {
    val allFonts: Flow<List<FontItem>> = fontDao.getAllFonts()
    val featuredFonts: Flow<List<FontItem>> = fontDao.getFeaturedFonts()
    val favoriteFonts: Flow<List<FontItem>> = fontDao.getFavoriteFonts()
    val downloadedFonts: Flow<List<FontItem>> = fontDao.getDownloadedFonts()
    val importedFonts: Flow<List<FontItem>> = fontDao.getImportedFonts()
    val arabicFonts: Flow<List<FontItem>> = fontDao.getArabicFonts()
    val englishFonts: Flow<List<FontItem>> = fontDao.getEnglishFonts()

    fun searchFonts(query: String): Flow<List<FontItem>> = fontDao.searchFonts(query)
    fun getFontById(id: String): Flow<FontItem?> = fontDao.getFontById(id)

    suspend fun initializeStarterFonts() = withContext(Dispatchers.IO) {
        val fontsDir = File(context.filesDir, "fonts")
        if (!fontsDir.exists()) {
            fontsDir.mkdirs()
        }

        val starters = listOf(
            StarterFontSpec(
                id = "cairo_arabic",
                name = "Cairo Modern",
                arabicName = "كايرو العصري",
                descriptionAr = "خط عربي وإنجليزي معاصر بتصميم هندسي فائق النقاء، مثالي للقراءة اليومية على الشاشات.",
                descriptionEn = "A contemporary geometric Arabic and Latin typeface designed for high legibility on screens.",
                author = "Mohamed Gaber",
                previewSampleAr = "أبجد هوز حطي كلمن سعفص قرشت - تجربة خط كايرو",
                previewSampleEn = "The quick brown fox jumps over the lazy dog 1234567890",
                assetName = "cairo.ttf",
                language = "Multilingual",
                category = "Modern",
                tags = "arabic,cairo,modern,sans,clean,hyperos,oneui",
                isFeatured = true,
                downloadCount = 48500
            ),
            StarterFontSpec(
                id = "amiri_naskh",
                name = "Amiri Classical",
                arabicName = "الأميري الكلاسيكي",
                descriptionAr = "خط نسخي كلاسيكي بديع مستوحى من خط مطبعة بولاق التاريخية، يعكس أصالة الحرف العربي.",
                descriptionEn = "A classical Arabic typeface in Naskh style revival of the beautiful Bulaq Press typography.",
                author = "Khaled Hosny",
                previewSampleAr = "قل إن هدى الله هو الهدى - خط الأميري النسخي الأصيل",
                previewSampleEn = "In the name of heritage and classical Arabic art",
                assetName = "amiri.ttf",
                language = "Arabic",
                category = "Calligraphy",
                tags = "arabic,amiri,naskh,calligraphy,classic,traditional",
                isFeatured = true,
                downloadCount = 39200
            ),
            StarterFontSpec(
                id = "tajawal_clean",
                name = "Tajawal Sans",
                arabicName = "تجوّل النظيف",
                descriptionAr = "خط هادئ وأنيق يجمع بين الحداثة وسهولة القراءة، مصمم خصيصًا للهواتف الذكية والتطبيقات.",
                descriptionEn = "A peaceful geometric sans-serif typeface crafted for digital mobile interfaces.",
                author = "Boutros Fonts",
                previewSampleAr = "تجوّل بحرية في عالم التخصيص والأناقة مع FontX",
                previewSampleEn = "Explore the future of mobile font customization",
                assetName = "tajawal.ttf",
                language = "Multilingual",
                category = "Modern",
                tags = "arabic,tajawal,modern,minimal,sans,clean",
                isFeatured = true,
                downloadCount = 31800
            ),
            StarterFontSpec(
                id = "outfit_sans",
                name = "Outfit Geometric",
                arabicName = "أوتفيت الهندسي",
                descriptionAr = "خط لاتيني هندسي حديث مفعم بالحيوية والانتعاش، يضفي طابعًا مستقبليًا على جهازك.",
                descriptionEn = "A fresh, brand-inspired geometric typeface blending modern curves and clean lines.",
                author = "Outfit Studios",
                previewSampleAr = "نمط خط عالمي متوافق مع كافة واجهات الأندرويد الحديثة",
                previewSampleEn = "Custom Typography at its Finest 2026",
                assetName = "outfit.ttf",
                language = "English",
                category = "iOS Style",
                tags = "english,outfit,geometric,ios,modern,sans",
                isFeatured = true,
                downloadCount = 27400
            ),
            StarterFontSpec(
                id = "playfair_serif",
                name = "Playfair Deluxe",
                arabicName = "بلايفير الكلاسيكي الفاخر",
                descriptionAr = "خط سيريف راقٍ ومترف مستوحى من حقبة التنوير الكلاسيكية، يضفي فخامة استثنائية.",
                descriptionEn = "A transitional luxury serif font with elegant contrast and high editorial sophistication.",
                author = "Claus Eggers Sørensen",
                previewSampleAr = "أناقة الحرف وتفاصيل الفخامة في كل شاشة",
                previewSampleEn = "Editorial Elegance & Timeless Craftsmanship",
                assetName = "playfair.ttf",
                language = "Multilingual",
                category = "Elegant",
                tags = "serif,playfair,luxury,classic,editorial,elegant",
                isFeatured = false,
                downloadCount = 18900
            ),
            StarterFontSpec(
                id = "gaming_mono",
                name = "Cyber Pixel Mono",
                arabicName = "سايبر بكسل للألعاب",
                descriptionAr = "خط أحادي المسافة قوي وجريء مخصص لعشاق الألعاب والبرمجة والتصميم المستقبلي.",
                descriptionEn = "A bold monospaced powerhouse font designed for gaming, coding, and cyber aesthetics.",
                author = "DejaVu Fonts",
                previewSampleAr = "النظام: تم تفعيل وضع الألعاب الفائق بنجاح",
                previewSampleEn = "LEVEL 99 UNLOCKED // SYSTEM STATUS: ACTIVE",
                assetName = "gaming_pixel.ttf",
                language = "Multilingual",
                category = "Gaming",
                tags = "gaming,cyber,mono,pixel,retro,bold",
                isFeatured = false,
                downloadCount = 15300
            )
        )

        val fontItems = mutableListOf<FontItem>()

        for (spec in starters) {
            val destination = File(fontsDir, spec.assetName)
            if (!destination.exists() || destination.length() == 0L) {
                try {
                    context.assets.open("fonts/${spec.assetName}").use { input ->
                        FileOutputStream(destination).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {
                    // ignore if asset missing
                }
            }

            val fileSize = if (destination.exists()) destination.length() else 450000L

            fontItems.add(
                FontItem(
                    id = spec.id,
                    name = spec.name,
                    arabicName = spec.arabicName,
                    descriptionAr = spec.descriptionAr,
                    descriptionEn = spec.descriptionEn,
                    author = spec.author,
                    previewSampleAr = spec.previewSampleAr,
                    previewSampleEn = spec.previewSampleEn,
                    assetFileName = spec.assetName,
                    localFilePath = destination.absolutePath,
                    format = "TTF",
                    sizeBytes = fileSize,
                    language = spec.language,
                    category = spec.category,
                    tags = spec.tags,
                    isFeatured = spec.isFeatured,
                    isDownloaded = true,
                    isFavorite = false,
                    isImported = false,
                    downloadCount = spec.downloadCount
                )
            )
        }

        fontDao.insertFonts(fontItems)
    }

    suspend fun toggleFavorite(fontId: String, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        fontDao.updateFavorite(fontId, !currentStatus)
    }

    suspend fun markAsUsed(fontId: String) = withContext(Dispatchers.IO) {
        fontDao.updateLastUsed(fontId, System.currentTimeMillis())
    }

    suspend fun downloadFont(font: FontItem): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fontsDir = File(context.filesDir, "fonts")
            if (!fontsDir.exists()) fontsDir.mkdirs()

            val targetFile = File(fontsDir, font.assetFileName.ifBlank { "${font.id}.ttf" })
            if (!targetFile.exists() && font.assetFileName.isNotBlank()) {
                context.assets.open("fonts/${font.assetFileName}").use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            fontDao.updateDownloadStatus(font.id, true, targetFile.absolutePath)
            Result.success(targetFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importFontFromUri(uri: Uri): Result<FontItem> = withContext(Dispatchers.IO) {
        try {
            var fileName = "imported_font_${System.currentTimeMillis()}.ttf"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    val displayName = cursor.getString(nameIndex)
                    if (!displayName.isNullOrBlank()) {
                        fileName = displayName
                    }
                }
            }

            val extension = fileName.substringAfterLast(".", "").lowercase()
            if (extension != "ttf" && extension != "otf") {
                return@withContext Result.failure(
                    IllegalArgumentException("الملف المختار ليس بصيغة TTF أو OTF صالحة.")
                )
            }

            val fontsDir = File(context.filesDir, "fonts")
            if (!fontsDir.exists()) fontsDir.mkdirs()

            val sanitizedName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val targetFile = File(fontsDir, "imported_${System.currentTimeMillis()}_$sanitizedName")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext Result.failure(IllegalStateException("تعذر فتح ملف الخط."))

            // Validate header signature
            if (!FontRenderManager.validateFontFile(targetFile)) {
                targetFile.delete()
                return@withContext Result.failure(
                    IllegalArgumentException("الملف تالف أو ليس ملف خط حقيقي.")
                )
            }

            val baseName = fileName.substringBeforeLast(".")
                .replace("_", " ")
                .replace("-", " ")
                .trim()
                .replaceFirstChar { it.uppercase() }

            val fontItem = FontItem(
                id = "imported_${UUID.randomUUID()}",
                name = baseName,
                arabicName = baseName,
                descriptionAr = "خط مستورد محليًا بواسطة المستخدم.",
                descriptionEn = "Locally imported custom font file.",
                author = "User Import",
                previewSampleAr = "تجربة الخط المستورد بنجاح في تطبيق FontX",
                previewSampleEn = "Imported typography test preview in FontX",
                assetFileName = "",
                localFilePath = targetFile.absolutePath,
                format = extension.uppercase(),
                sizeBytes = targetFile.length(),
                language = "Multilingual",
                category = "Modern",
                tags = "imported,custom,local",
                isFeatured = false,
                isDownloaded = true,
                isFavorite = false,
                isImported = true,
                downloadCount = 1,
                lastUsedTimestamp = System.currentTimeMillis()
            )

            fontDao.insertFont(fontItem)
            Result.success(fontItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportFont(font: FontItem): Result<File> = withContext(Dispatchers.IO) {
        try {
            val localPath = font.localFilePath
            val sourceFile = if (!localPath.isNullOrBlank()) File(localPath) else null

            if (sourceFile == null || !sourceFile.exists()) {
                return@withContext Result.failure(IllegalStateException("ملف الخط غير متوفر محليًا للتصدير."))
            }

            val exported = DeviceCompatibilityManager.exportFont(
                context = context,
                fontFile = sourceFile,
                fontTitle = font.name
            )

            if (exported != null && exported.exists()) {
                Result.success(exported)
            } else {
                Result.failure(IllegalStateException("فشل تصدير حزمة الخط."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearFontCache(): Long = withContext(Dispatchers.IO) {
        var freedBytes = 0L
        try {
            FontRenderManager.clearCache()
            val exportDir = File(context.getExternalFilesDir(null) ?: context.filesDir, "ExportedFonts")
            if (exportDir.exists()) {
                exportDir.listFiles()?.forEach {
                    freedBytes += it.length()
                    it.delete()
                }
            }
            context.cacheDir.listFiles()?.forEach {
                freedBytes += it.length()
                it.deleteRecursively()
            }
        } catch (e: Exception) {
            // ignore
        }
        freedBytes
    }

    private data class StarterFontSpec(
        val id: String,
        val name: String,
        val arabicName: String,
        val descriptionAr: String,
        val descriptionEn: String,
        val author: String,
        val previewSampleAr: String,
        val previewSampleEn: String,
        val assetName: String,
        val language: String,
        val category: String,
        val tags: String,
        val isFeatured: Boolean,
        val downloadCount: Int
    )
}
