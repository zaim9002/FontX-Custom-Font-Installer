package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manager.FontRenderManager
import com.example.model.DeviceInfo
import com.example.model.FontItem
import com.example.ui.components.DeviceStatusCard
import com.example.ui.components.FontCard
import com.example.ui.components.FontFilterChips
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    deviceInfo: DeviceInfo,
    fonts: List<FontItem>,
    featuredFonts: List<FontItem>,
    arabicFonts: List<FontItem>,
    englishFonts: List<FontItem>,
    searchQuery: String,
    selectedCategory: String,
    isArabic: Boolean,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onFontClick: (FontItem) -> Unit,
    onToggleFavorite: (FontItem) -> Unit,
    onCheckCompatibility: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSearching = searchQuery.isNotBlank() || selectedCategory != "All"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // App Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(listOf(PrimaryViolet, SecondaryCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = "FontX",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "FontX",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = if (isArabic) "مدير ومخصص خطوط الهاتف" else "Phone Font Manager",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Language toggle pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .testTag("language_toggle_btn")
                            .clickable { onToggleLanguage() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Toggle Language",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "English" else "العربية",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_text_field"),
                    placeholder = {
                        Text(
                            text = if (isArabic) "ابحث عن خط، تصنيف، لغة…" else "Search fonts, categories, tags…",
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
            }
        }

        // Device Status Card
        item {
            DeviceStatusCard(
                deviceInfo = deviceInfo,
                isArabic = isArabic,
                onCheckCompatibility = onCheckCompatibility,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Category Filter Chips
        item {
            Spacer(modifier = Modifier.height(4.dp))
            FontFilterChips(
                selectedCategory = selectedCategory,
                isArabic = isArabic,
                onCategorySelected = onCategoryChange
            )
        }

        if (isSearching) {
            // Search / Filtered Results
            item {
                Text(
                    text = if (isArabic) "نتائج البحث (${fonts.size})" else "Results (${fonts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
                )
            }

            if (fonts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) "لم يتم العثور على خطوط مطابقة" else "No matching fonts found",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(fonts, key = { it.id }) { font ->
                    FontCard(
                        font = font,
                        isArabic = isArabic,
                        onFontClick = { onFontClick(font) },
                        onToggleFavorite = { onToggleFavorite(font) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        } else {
            // Standard Curated Sections

            // Featured Carousel
            if (featuredFonts.isNotEmpty()) {
                item {
                    Text(
                        text = if (isArabic) "خطوط مميزة ومختارة" else "Featured Fonts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featuredFonts, key = { it.id }) { font ->
                            FeaturedFontMiniCard(
                                font = font,
                                isArabic = isArabic,
                                onClick = { onFontClick(font) }
                            )
                        }
                    }
                }
            }

            // Arabic Fonts Section
            if (arabicFonts.isNotEmpty()) {
                item {
                    Text(
                        text = if (isArabic) "روائع الخط العربي" else "Arabic Fonts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
                    )
                }
                items(arabicFonts.take(3), key = { it.id }) { font ->
                    FontCard(
                        font = font,
                        isArabic = isArabic,
                        onFontClick = { onFontClick(font) },
                        onToggleFavorite = { onToggleFavorite(font) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // English / Global Fonts Section
            if (englishFonts.isNotEmpty()) {
                item {
                    Text(
                        text = if (isArabic) "الخطوط الإنجليزية والعالمية" else "English & Global Fonts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
                    )
                }
                items(englishFonts.take(3), key = { it.id }) { font ->
                    FontCard(
                        font = font,
                        isArabic = isArabic,
                        onFontClick = { onFontClick(font) },
                        onToggleFavorite = { onToggleFavorite(font) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturedFontMiniCard(
    font: FontItem,
    isArabic: Boolean,
    onClick: () -> Unit
) {
    val fontFamily = remember(font.localFilePath) {
        FontRenderManager.getFontFamily(font.localFilePath)
    }

    Card(
        modifier = Modifier
            .width(220.dp)
            .testTag("featured_font_${font.id}")
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) font.arabicName else font.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isArabic) font.previewSampleAr else font.previewSampleEn,
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${font.category} • ${font.format}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
