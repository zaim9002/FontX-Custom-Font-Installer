package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manager.FontRenderManager
import com.example.model.FontItem
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontDetailScreen(
    font: FontItem,
    isArabic: Boolean,
    customText: String,
    fontSizeSp: Float,
    isBold: Boolean,
    isItalic: Boolean,
    onBack: () -> Unit,
    onCustomTextChange: (String) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    onApplyFont: () -> Unit,
    onExportFont: () -> Unit,
    onDownloadFont: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = remember(font.localFilePath) {
        FontRenderManager.getFontFamily(font.localFilePath)
    }

    val sampleArabic = "مرحبًا بك في FontX - تجربة الخطوط الأكثر احترافية"
    val sampleEnglish = "Welcome to FontX - Customize your typography style"
    val sampleNumbers = "0123456789 - ١٢٣٤٥٦٧٨٩٠"
    val sampleAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz"

    val currentText = if (customText.isNotBlank()) {
        customText
    } else {
        if (isArabic) font.previewSampleAr else font.previewSampleEn
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("font_detail_screen")
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = if (isArabic) font.arabicName else font.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("detail_favorite_btn")
                ) {
                    Icon(
                        imageVector = if (font.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (font.isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Interactive Font Preview Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isArabic) "المعاينة الحية التفاعلية" else "Live Interactive Preview",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${fontSizeSp.toInt()} sp",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Rendered Text Box using real font!
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = currentText,
                                    fontFamily = fontFamily,
                                    fontSize = fontSizeSp.sp,
                                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    lineHeight = (fontSizeSp * 1.35f).sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Controls: Size slider & Bold/Italic
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Slider(
                                value = fontSizeSp,
                                onValueChange = onFontSizeChange,
                                valueRange = 14f..44f,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("font_size_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = onToggleBold,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatBold,
                                    contentDescription = "Bold",
                                    tint = if (isBold) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = onToggleItalic,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isItalic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatItalic,
                                    contentDescription = "Italic",
                                    tint = if (isItalic) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Editable Input & Quick Preset Samples
            item {
                Column {
                    Text(
                        text = if (isArabic) "تخصيص نص المعاينة" else "Customize Preview Text",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = customText,
                        onValueChange = onCustomTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_preview_input"),
                        placeholder = {
                            Text(text = if (isArabic) "اكتب أي نص لمعاينته بالخط الحالي…" else "Type any text to preview…")
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = customText == sampleArabic,
                            onClick = { onCustomTextChange(sampleArabic) },
                            label = { Text("عربي", fontSize = 12.sp) }
                        )
                        FilterChip(
                            selected = customText == sampleEnglish,
                            onClick = { onCustomTextChange(sampleEnglish) },
                            label = { Text("English", fontSize = 12.sp) }
                        )
                        FilterChip(
                            selected = customText == sampleNumbers,
                            onClick = { onCustomTextChange(sampleNumbers) },
                            label = { Text("012345", fontSize = 12.sp) }
                        )
                        FilterChip(
                            selected = customText == sampleAlphabet,
                            onClick = { onCustomTextChange(sampleAlphabet) },
                            label = { Text("ABC", fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Font Details & Specs
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isArabic) "تفاصيل ومواصفات الخط" else "Font Specifications",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isArabic) font.descriptionAr else font.descriptionEn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecItem(label = if (isArabic) "المصمم" else "Author", value = font.author)
                            SpecItem(label = if (isArabic) "الصيغة" else "Format", value = font.format)
                            SpecItem(label = if (isArabic) "التصنيف" else "Category", value = font.category)
                            SpecItem(
                                label = if (isArabic) "الحجم" else "Size",
                                value = "%.1f KB".format(font.sizeBytes / 1024.0)
                            )
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onApplyFont,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("apply_font_primary_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "تطبيق الخط على الهاتف" else "Apply Font to Phone",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onExportFont,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("export_font_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "تصدير الملف" else "Export File",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = onDownloadFont,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("download_font_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "جاهز للتطبيق" else "Ready",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
