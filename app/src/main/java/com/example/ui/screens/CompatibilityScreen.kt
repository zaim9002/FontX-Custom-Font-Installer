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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceInfo
import com.example.model.FontSupportLevel
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun CompatibilityScreen(
    deviceInfo: DeviceInfo,
    isArabic: Boolean,
    onOpenSettings: () -> Unit,
    onOpenThemeStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (deviceInfo.supportLevel) {
        FontSupportLevel.NATIVE_THEME_ENGINE -> StatusSuccess
        FontSupportLevel.SETTINGS_FONT_PICKER -> Color(0xFF38BDF8)
        FontSupportLevel.RESTRICTED_MANUAL_GUIDE -> StatusWarning
    }

    val statusIcon = when (deviceInfo.supportLevel) {
        FontSupportLevel.RESTRICTED_MANUAL_GUIDE -> Icons.Default.Warning
        else -> Icons.Default.CheckCircle
    }

    val statusLabel = when (deviceInfo.supportLevel) {
        FontSupportLevel.NATIVE_THEME_ENGINE -> if (isArabic) "مدعوم بالكامل (محرك ثيمات مدمج)" else "Fully Supported (Theme Engine)"
        FontSupportLevel.SETTINGS_FONT_PICKER -> if (isArabic) "مدعوم (عبر إعدادات الشاشة والخط)" else "Supported (Display & Font Settings)"
        FontSupportLevel.RESTRICTED_MANUAL_GUIDE -> if (isArabic) "يتطلب تثبيت يدوي / تصدير (حماية أندرويد)" else "Manual Export Required (Android Policy)"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("compatibility_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title
        item {
            Column {
                Text(
                    text = if (isArabic) "فحص توافق الهاتف ومحرك الخطوط" else "Device Compatibility Engine",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isArabic) "تقرير التحليل التلقائي لأجهزة أندرويد وطريقة التخصيص المناسبة" else "Automated Android hardware & OEM font engine analysis",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Detailed Hardware & Rom Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "${deviceInfo.brand} ${deviceInfo.model}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "واجهة النظام: ${deviceInfo.romName}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoPill(label = if (isArabic) "الشركة المصنعة" else "Manufacturer", value = deviceInfo.manufacturer)
                        InfoPill(label = if (isArabic) "إصدار أندرويد" else "Android Version", value = "Android ${deviceInfo.androidVersion}")
                        InfoPill(label = if (isArabic) "مستوى SDK" else "API Level", value = "API ${deviceInfo.sdkInt}")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Status Badge Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusColor.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = statusLabel,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isArabic) deviceInfo.summaryAr else deviceInfo.summaryEn,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Detailed Explanation
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SecondaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "تحليل دعم خطوط النظام لهذا الجهاز" else "System Font Capability Analysis",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isArabic) deviceInfo.detailedExplanationAr else deviceInfo.detailedExplanationEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Installation Steps Checklist
        item {
            Text(
                text = if (isArabic) "طريقة التثبيت المعتمدة لجهازك" else "Recommended Installation Steps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(deviceInfo.guideSteps) { step ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepNumber}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isArabic) step.titleAr else step.titleEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic) step.descriptionAr else step.descriptionEn,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Interactive Testing Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("compat_open_settings_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "فتح إعدادات الخط والشاشة بالجهاز" else "Open Display & Font Settings",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onOpenThemeStore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("compat_open_themestore_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "اختبار فتح متجر الثيمات الرسمي" else "Open Official Theme Store",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Safety Policy Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StatusSuccess.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = StatusSuccess.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isArabic) "ميثاق الأمان والاستقرار في FontX" else "FontX Security & Safety Commitment",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = StatusSuccess
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic) {
                                "لا يستخدم هذا التطبيق أي روت أو ثغرات أمنية أو تعديلات غير مصرح بها. نحن نلتزم بالمعايير الرسمية لكل شركة مصنعة لضمان سلامة جهازك وبياناتك 100%."
                            } else {
                                "FontX never requires root or private exploits. We strictly follow each OEM's official customization APIs and settings to guarantee 100% device stability."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoPill(label: String, value: String) {
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
