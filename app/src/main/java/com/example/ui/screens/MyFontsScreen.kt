package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FontItem
import com.example.ui.components.FontCard
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.SecondaryCyan

@Composable
fun MyFontsScreen(
    downloadedFonts: List<FontItem>,
    importedFonts: List<FontItem>,
    favoriteFonts: List<FontItem>,
    allFonts: List<FontItem>,
    isArabic: Boolean,
    onFontClick: (FontItem) -> Unit,
    onToggleFavorite: (FontItem) -> Unit,
    onImportFont: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { onImportFont(it) }
    }

    val tabTitles = if (isArabic) {
        listOf("المحملة", "المستوردة", "المفضلة", "سجل الاستخدام")
    } else {
        listOf("Downloaded", "Imported", "Favorites", "Recent")
    }

    val recentFonts = remember(allFonts) {
        allFonts.filter { it.lastUsedTimestamp > 0 }.sortedByDescending { it.lastUsedTimestamp }
    }

    val displayedFonts = when (selectedTab) {
        0 -> downloadedFonts
        1 -> importedFonts
        2 -> favoriteFonts
        else -> recentFonts
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("my_fonts_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isArabic) "مدير خطوطي" else "My Fonts Manager",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isArabic) "إدارة الخطوط المحملة والمستوردة والمفضلة" else "Manage downloaded, imported & favorite fonts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    val count = when (index) {
                        0 -> downloadedFonts.size
                        1 -> importedFonts.size
                        2 -> favoriteFonts.size
                        else -> recentFonts.size
                    }
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = "$title ($count)",
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // Fonts List or Empty State
            if (displayedFonts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (selectedTab) {
                                        0 -> Icons.Default.CloudDownload
                                        1 -> Icons.Default.UploadFile
                                        2 -> Icons.Default.Favorite
                                        else -> Icons.Default.History
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = when (selectedTab) {
                                    0 -> if (isArabic) "لا توجد خطوط محملة بعد" else "No downloaded fonts yet"
                                    1 -> if (isArabic) "لا توجد خطوط مستوردة" else "No imported fonts yet"
                                    2 -> if (isArabic) "لا توجد خطوط في المفضلة" else "No favorites added yet"
                                    else -> if (isArabic) "لا توجد خطوط مستخدمة مؤخرًا" else "No recent font activity"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = when (selectedTab) {
                                    0 -> if (isArabic) "تصفح المكتبة وحمل خطوطك لتطبيقها فورًا." else "Browse the library and download fonts to apply them."
                                    1 -> if (isArabic) "اضغط على زر (+) في الأسفل لاختيار ملف .ttf أو .otf من جهازك." else "Tap the (+) button below to select a .ttf or .otf file from your device."
                                    2 -> if (isArabic) "اضغط على أيقونة القلب في أي خط لإضافته إلى قائمتك المفضلة." else "Tap the heart icon on any font to bookmark it here."
                                    else -> if (isArabic) "عاين الخطوط لتظهر في سجل الاستخدام السريع." else "Preview and test fonts to see them in your history."
                                },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedFonts, key = { it.id }) { font ->
                        FontCard(
                            font = font,
                            isArabic = isArabic,
                            onFontClick = { onFontClick(font) },
                            onToggleFavorite = { onToggleFavorite(font) }
                        )
                    }
                }
            }
        }

        // Import Font FAB
        FloatingActionButton(
            onClick = {
                documentPickerLauncher.launch(
                    arrayOf(
                        "font/ttf",
                        "font/otf",
                        "application/x-font-ttf",
                        "application/x-font-opentype",
                        "application/octet-stream",
                        "*/*"
                    )
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 80.dp)
                .testTag("import_font_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Import"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "استيراد TTF/OTF" else "Import Font",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
