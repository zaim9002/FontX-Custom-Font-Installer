package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ApplyFontDialog
import com.example.ui.screens.CompatibilityScreen
import com.example.ui.screens.FontDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyFontsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FontViewModel

enum class MainTab(val titleAr: String, val titleEn: String, val icon: ImageVector) {
    HOME("الرئيسية", "Home", Icons.Default.Home),
    MY_FONTS("خطوطي", "My Fonts", Icons.Default.Folder),
    COMPATIBILITY("التوافق", "Compatibility", Icons.Default.Build),
    SETTINGS("الإعدادات", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FontXApp()
        }
    }
}

@Composable
fun FontXApp(
    viewModel: FontViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isArabic = currentLanguage == "ar"

    val isDarkTheme = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(MainTab.HOME) }

    val deviceInfo by viewModel.deviceInfo.collectAsStateWithLifecycle()
    val allFonts by viewModel.allFonts.collectAsStateWithLifecycle()
    val filteredFonts by viewModel.filteredFonts.collectAsStateWithLifecycle()
    val featuredFonts by viewModel.featuredFonts.collectAsStateWithLifecycle()
    val favoriteFonts by viewModel.favoriteFonts.collectAsStateWithLifecycle()
    val downloadedFonts by viewModel.downloadedFonts.collectAsStateWithLifecycle()
    val importedFonts by viewModel.importedFonts.collectAsStateWithLifecycle()
    val arabicFonts by viewModel.arabicFonts.collectAsStateWithLifecycle()
    val englishFonts by viewModel.englishFonts.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedFontForDetail by viewModel.selectedFontForDetail.collectAsStateWithLifecycle()
    val customPreviewText by viewModel.customPreviewText.collectAsStateWithLifecycle()
    val previewFontSize by viewModel.previewFontSizeSp.collectAsStateWithLifecycle()
    val previewIsBold by viewModel.previewIsBold.collectAsStateWithLifecycle()
    val previewIsItalic by viewModel.previewIsItalic.collectAsStateWithLifecycle()
    val activeApplyDialogFont by viewModel.activeApplyDialogFont.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val isWifiOnly by viewModel.isWifiOnly.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissMessage()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        MyApplicationTheme(darkTheme = isDarkTheme) {
            if (showSplash) {
                SplashScreen(
                    deviceInfo = deviceInfo,
                    isArabic = isArabic,
                    onFinish = { showSplash = false }
                )
            } else {
                // Back button handling
                BackHandler(enabled = selectedFontForDetail != null || currentTab != MainTab.HOME) {
                    if (selectedFontForDetail != null) {
                        viewModel.selectFontForDetail(null)
                    } else if (currentTab != MainTab.HOME) {
                        currentTab = MainTab.HOME
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        if (selectedFontForDetail == null) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 10.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 0.dp,
                                    modifier = Modifier.navigationBarsPadding()
                                ) {
                                    MainTab.values().forEach { tab ->
                                        val isSelected = currentTab == tab
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = { currentTab = tab },
                                            icon = {
                                                Icon(
                                                    imageVector = tab.icon,
                                                    contentDescription = if (isArabic) tab.titleAr else tab.titleEn,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = if (isArabic) tab.titleAr else tab.titleEn,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val detailFont = selectedFontForDetail
                        if (detailFont != null) {
                            FontDetailScreen(
                                font = detailFont,
                                isArabic = isArabic,
                                customText = customPreviewText,
                                fontSizeSp = previewFontSize,
                                isBold = previewIsBold,
                                isItalic = previewIsItalic,
                                onBack = { viewModel.selectFontForDetail(null) },
                                onCustomTextChange = { viewModel.setCustomPreviewText(it) },
                                onFontSizeChange = { viewModel.setPreviewFontSize(it) },
                                onToggleBold = { viewModel.toggleBold() },
                                onToggleItalic = { viewModel.toggleItalic() },
                                onApplyFont = { viewModel.showApplyDialog(detailFont) },
                                onExportFont = { viewModel.exportFont(detailFont) },
                                onDownloadFont = { viewModel.downloadFont(detailFont) },
                                onToggleFavorite = { viewModel.toggleFavorite(detailFont) }
                            )
                        } else {
                            when (currentTab) {
                                MainTab.HOME -> {
                                    HomeScreen(
                                        deviceInfo = deviceInfo,
                                        fonts = filteredFonts,
                                        featuredFonts = featuredFonts,
                                        arabicFonts = arabicFonts,
                                        englishFonts = englishFonts,
                                        searchQuery = searchQuery,
                                        selectedCategory = selectedCategory,
                                        isArabic = isArabic,
                                        onSearchChange = { viewModel.setSearchQuery(it) },
                                        onCategoryChange = { viewModel.setCategory(it) },
                                        onFontClick = { viewModel.selectFontForDetail(it) },
                                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                                        onCheckCompatibility = { currentTab = MainTab.COMPATIBILITY },
                                        onToggleLanguage = {
                                            viewModel.setLanguage(if (isArabic) "en" else "ar")
                                        }
                                    )
                                }
                                MainTab.MY_FONTS -> {
                                    MyFontsScreen(
                                        downloadedFonts = downloadedFonts,
                                        importedFonts = importedFonts,
                                        favoriteFonts = favoriteFonts,
                                        allFonts = allFonts,
                                        isArabic = isArabic,
                                        onFontClick = { viewModel.selectFontForDetail(it) },
                                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                                        onImportFont = { viewModel.importFont(it) }
                                    )
                                }
                                MainTab.COMPATIBILITY -> {
                                    CompatibilityScreen(
                                        deviceInfo = deviceInfo,
                                        isArabic = isArabic,
                                        onOpenSettings = {
                                            val intent = com.example.compat.DeviceCompatibilityManager.createApplyIntent(context)
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                // ignore
                                            }
                                        },
                                        onOpenThemeStore = {
                                            viewModel.launchThemeStore(context)
                                        }
                                    )
                                }
                                MainTab.SETTINGS -> {
                                    SettingsScreen(
                                        currentLanguage = currentLanguage,
                                        themeMode = themeMode,
                                        isWifiOnly = isWifiOnly,
                                        isArabic = isArabic,
                                        onLanguageChange = { viewModel.setLanguage(it) },
                                        onThemeModeChange = { viewModel.setThemeMode(it) },
                                        onWifiOnlyToggle = { viewModel.setWifiOnly(it) },
                                        onClearCache = { viewModel.clearCache() }
                                    )
                                }
                            }
                        }

                        // Apply Font OEM Dialog
                        val applyFont = activeApplyDialogFont
                        if (applyFont != null) {
                            ApplyFontDialog(
                                font = applyFont,
                                deviceInfo = deviceInfo,
                                isArabic = isArabic,
                                onDismiss = { viewModel.dismissApplyDialog() },
                                onOpenSettings = {
                                    viewModel.dismissApplyDialog()
                                    viewModel.launchApplyIntent(context, applyFont)
                                },
                                onExportFont = {
                                    viewModel.dismissApplyDialog()
                                    viewModel.exportFont(applyFont)
                                },
                                onOpenThemeStore = {
                                    viewModel.dismissApplyDialog()
                                    viewModel.launchThemeStore(context)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
