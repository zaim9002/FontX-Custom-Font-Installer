package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.compat.DeviceCompatibilityManager
import com.example.data.FontDatabase
import com.example.data.FontRepository
import com.example.model.DeviceInfo
import com.example.model.FontItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class FontViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FontRepository

    val deviceInfo: StateFlow<DeviceInfo>

    val allFonts: StateFlow<List<FontItem>>
    val featuredFonts: StateFlow<List<FontItem>>
    val favoriteFonts: StateFlow<List<FontItem>>
    val downloadedFonts: StateFlow<List<FontItem>>
    val importedFonts: StateFlow<List<FontItem>>
    val arabicFonts: StateFlow<List<FontItem>>
    val englishFonts: StateFlow<List<FontItem>>

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedFontForDetail = MutableStateFlow<FontItem?>(null)
    val selectedFontForDetail: StateFlow<FontItem?> = _selectedFontForDetail.asStateFlow()

    private val _customPreviewText = MutableStateFlow("")
    val customPreviewText: StateFlow<String> = _customPreviewText.asStateFlow()

    private val _previewFontSizeSp = MutableStateFlow(24f)
    val previewFontSizeSp: StateFlow<Float> = _previewFontSizeSp.asStateFlow()

    private val _previewIsBold = MutableStateFlow(false)
    val previewIsBold: StateFlow<Boolean> = _previewIsBold.asStateFlow()

    private val _previewIsItalic = MutableStateFlow(false)
    val previewIsItalic: StateFlow<Boolean> = _previewIsItalic.asStateFlow()

    private val _activeApplyDialogFont = MutableStateFlow<FontItem?>(null)
    val activeApplyDialogFont: StateFlow<FontItem?> = _activeApplyDialogFont.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _currentLanguage = MutableStateFlow("ar") // "ar" or "en"
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow("dark") // "dark", "light", "system"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _isWifiOnly = MutableStateFlow(false)
    val isWifiOnly: StateFlow<Boolean> = _isWifiOnly.asStateFlow()

    val filteredFonts: StateFlow<List<FontItem>>

    init {
        val db = FontDatabase.getDatabase(application)
        repository = FontRepository(application, db.fontDao())

        val detectedInfo = DeviceCompatibilityManager.getDeviceInfo(application)
        deviceInfo = MutableStateFlow(detectedInfo).asStateFlow()

        allFonts = repository.allFonts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        featuredFonts = repository.featuredFonts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        favoriteFonts = repository.favoriteFonts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        downloadedFonts = repository.downloadedFonts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        importedFonts = repository.importedFonts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        arabicFonts = repository.arabicFonts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        englishFonts = repository.englishFonts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        filteredFonts = combine(allFonts, _searchQuery, _selectedCategory) { fonts, query, category ->
            fonts.filter { font ->
                val matchesQuery = if (query.isBlank()) {
                    true
                } else {
                    font.name.contains(query, ignoreCase = true) ||
                            font.arabicName.contains(query, ignoreCase = true) ||
                            font.tags.contains(query, ignoreCase = true) ||
                            font.category.contains(query, ignoreCase = true) ||
                            font.author.contains(query, ignoreCase = true)
                }

                val matchesCategory = when (category) {
                    "All", "الكل" -> true
                    "Arabic", "عربي" -> font.language == "Arabic" || font.language == "Multilingual"
                    "English", "إنجليزي" -> font.language == "English" || font.language == "Multilingual"
                    else -> font.category.equals(category, ignoreCase = true)
                }

                matchesQuery && matchesCategory
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initializeStarterFonts()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectFontForDetail(font: FontItem?) {
        _selectedFontForDetail.value = font
        if (font != null) {
            viewModelScope.launch {
                repository.markAsUsed(font.id)
            }
        }
    }

    fun setCustomPreviewText(text: String) {
        _customPreviewText.value = text
    }

    fun setPreviewFontSize(sizeSp: Float) {
        _previewFontSizeSp.value = sizeSp.coerceIn(12f, 48f)
    }

    fun toggleBold() {
        _previewIsBold.value = !_previewIsBold.value
    }

    fun toggleItalic() {
        _previewIsItalic.value = !_previewIsItalic.value
    }

    fun toggleFavorite(font: FontItem) {
        viewModelScope.launch {
            repository.toggleFavorite(font.id, font.isFavorite)
        }
    }

    fun showApplyDialog(font: FontItem) {
        _activeApplyDialogFont.value = font
    }

    fun dismissApplyDialog() {
        _activeApplyDialogFont.value = null
    }

    fun importFont(uri: Uri) {
        viewModelScope.launch {
            val result = repository.importFontFromUri(uri)
            result.onSuccess { item ->
                _userMessage.value = if (_currentLanguage.value == "ar") {
                    "تم استيراد الخط (${item.name}) بنجاح وإضافته إلى خطوطي."
                } else {
                    "Font (${item.name}) imported successfully and added to My Fonts."
                }
                selectFontForDetail(item)
            }.onFailure { err ->
                _userMessage.value = err.localizedMessage ?: "فشل استيراد الخط"
            }
        }
    }

    fun exportFont(font: FontItem) {
        viewModelScope.launch {
            val result = repository.exportFont(font)
            result.onSuccess { file ->
                _userMessage.value = if (_currentLanguage.value == "ar") {
                    "تم تصدير ملف الخط بنجاح إلى: ${file.name}"
                } else {
                    "Font file exported successfully to: ${file.name}"
                }
            }.onFailure { err ->
                _userMessage.value = err.localizedMessage ?: "فشل تصدير الخط"
            }
        }
    }

    fun downloadFont(font: FontItem) {
        viewModelScope.launch {
            val result = repository.downloadFont(font)
            result.onSuccess {
                _userMessage.value = if (_currentLanguage.value == "ar") {
                    "تم تجهيز وتنزيل خط (${font.arabicName}) بنجاح!"
                } else {
                    "Font (${font.name}) downloaded and ready to apply!"
                }
            }.onFailure { err ->
                _userMessage.value = err.localizedMessage ?: "فشل تنزيل الخط"
            }
        }
    }

    fun launchApplyIntent(context: Context, font: FontItem) {
        val file = font.localFilePath?.let { File(it) }
        val intent = DeviceCompatibilityManager.createApplyIntent(context, file)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            _userMessage.value = "تعذر فتح شاشة الإعدادات تلقائيًا."
        }
    }

    fun launchThemeStore(context: Context) {
        val intent = DeviceCompatibilityManager.createThemeStoreIntent(context)
        if (intent != null) {
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                _userMessage.value = "تعذر فتح متجر المظاهر."
            }
        } else {
            _userMessage.value = "متجر المظاهر غير متوفر على هذا الجهاز."
        }
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    fun setWifiOnly(wifiOnly: Boolean) {
        _isWifiOnly.value = wifiOnly
    }

    fun clearCache() {
        viewModelScope.launch {
            val freedBytes = repository.clearFontCache()
            val mb = (freedBytes / (1024.0 * 1024.0))
            _userMessage.value = if (_currentLanguage.value == "ar") {
                "تم مسح الذاكرة المؤقتة بنجاح (تم تحرير %.1f MB).".format(mb)
            } else {
                "Cache cleared successfully (freed %.1f MB).".format(mb)
            }
        }
    }

    fun dismissMessage() {
        _userMessage.value = null
    }
}
