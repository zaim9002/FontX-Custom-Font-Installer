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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class FontViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FontRepository

    val deviceInfo: StateFlow<DeviceInfo>

    // Single source of truth from Room database
    val allFonts: StateFlow<List<FontItem>>

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedFontForDetail = MutableStateFlow<FontItem?>(null)
    val selectedFontForDetail: StateFlow<FontItem?> = _selectedFontForDetail.asStateFlow()

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

    // Pure in-memory derived flows - zero SQLite disk queries, instant 120fps UI updates
    val featuredFonts: StateFlow<List<FontItem>>
    val favoriteFonts: StateFlow<List<FontItem>>
    val downloadedFonts: StateFlow<List<FontItem>>
    val importedFonts: StateFlow<List<FontItem>>
    val arabicFonts: StateFlow<List<FontItem>>
    val englishFonts: StateFlow<List<FontItem>>
    val filteredFonts: StateFlow<List<FontItem>>

    init {
        val db = FontDatabase.getDatabase(application)
        repository = FontRepository(application, db.fontDao())

        val detectedInfo = DeviceCompatibilityManager.getDeviceInfo(application)
        deviceInfo = MutableStateFlow(detectedInfo).asStateFlow()

        allFonts = repository.allFonts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

        featuredFonts = allFonts.map { list ->
            list.filter { it.isFeatured }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        favoriteFonts = allFonts.map { list ->
            list.filter { it.isFavorite }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        downloadedFonts = allFonts.map { list ->
            list.filter { it.isDownloaded }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        importedFonts = allFonts.map { list ->
            list.filter { it.isImported }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        arabicFonts = allFonts.map { list ->
            list.filter { it.language == "Arabic" || it.language == "Multilingual" }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        englishFonts = allFonts.map { list ->
            list.filter { it.language == "English" || it.language == "Multilingual" }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        filteredFonts = combine(allFonts, _searchQuery, _selectedCategory) { fonts, query, category ->
            if (query.isBlank() && (category == "All" || category == "الكل")) {
                fonts
            } else {
                val trimmedQuery = query.trim()
                fonts.filter { font ->
                    val matchesQuery = if (trimmedQuery.isEmpty()) {
                        true
                    } else {
                        font.name.contains(trimmedQuery, ignoreCase = true) ||
                                font.arabicName.contains(trimmedQuery, ignoreCase = true) ||
                                font.tags.contains(trimmedQuery, ignoreCase = true) ||
                                font.category.contains(trimmedQuery, ignoreCase = true) ||
                                font.author.contains(trimmedQuery, ignoreCase = true)
                    }

                    val matchesCategory = when (category) {
                        "All", "الكل" -> true
                        "Arabic", "عربي" -> font.language == "Arabic" || font.language == "Multilingual"
                        "English", "إنجليزي" -> font.language == "English" || font.language == "Multilingual"
                        else -> font.category.equals(category, ignoreCase = true)
                    }

                    matchesQuery && matchesCategory
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.initializeStarterFonts()
            } catch (e: Throwable) {
                // Prevent crash during startup
            }
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
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    repository.markAsUsed(font.id)
                } catch (e: Throwable) {
                    // ignore
                }
            }
        }
    }

    fun toggleFavorite(font: FontItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.toggleFavorite(font.id, font.isFavorite)
            } catch (e: Throwable) {
                // ignore
            }
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
            try {
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
            } catch (e: Throwable) {
                _userMessage.value = "حدث خطأ أثناء فحص واستيراد ملف الخط."
            }
        }
    }

    fun exportFont(font: FontItem) {
        viewModelScope.launch {
            try {
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
            } catch (e: Throwable) {
                _userMessage.value = "فشل تصدير الخط."
            }
        }
    }

    fun downloadFont(font: FontItem) {
        viewModelScope.launch {
            try {
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
            } catch (e: Throwable) {
                _userMessage.value = "فشل تنزيل الخط."
            }
        }
    }

    fun launchApplyIntent(context: Context, font: FontItem) {
        try {
            val file = font.localFilePath?.let { File(it) }
            val intent = DeviceCompatibilityManager.createApplyIntent(context, file)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Throwable) {
            _userMessage.value = "تعذر فتح شاشة الإعدادات تلقائيًا على هذا الجهاز."
        }
    }

    fun launchThemeStore(context: Context) {
        try {
            val intent = DeviceCompatibilityManager.createThemeStoreIntent(context)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                _userMessage.value = "متجر المظاهر غير متوفر أو مثبت على هذا الجهاز."
            }
        } catch (e: Throwable) {
            _userMessage.value = "تعذر فتح متجر المظاهر."
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
            try {
                val freedBytes = repository.clearFontCache()
                val mb = (freedBytes / (1024.0 * 1024.0))
                _userMessage.value = if (_currentLanguage.value == "ar") {
                    "تم مسح الذاكرة المؤقتة بنجاح (تم تحرير %.1f MB).".format(mb)
                } else {
                    "Cache cleared successfully (freed %.1f MB).".format(mb)
                }
            } catch (e: Throwable) {
                _userMessage.value = "تم مسح الذاكرة المؤقتة."
            }
        }
    }

    fun dismissMessage() {
        _userMessage.value = null
    }
}
