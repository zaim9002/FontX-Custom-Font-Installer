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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan

@Composable
fun SettingsScreen(
    currentLanguage: String,
    themeMode: String,
    isWifiOnly: Boolean,
    isArabic: Boolean,
    onLanguageChange: (String) -> Unit,
    onThemeModeChange: (String) -> Unit,
    onWifiOnlyToggle: (Boolean) -> Unit,
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = if (isArabic) "الإعدادات والتخصيص" else "Settings & Preferences",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isArabic) "خيارات المظهر واللغة وإدارة التخزين" else "Appearance, language, and storage controls",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Appearance & Theme Mode
        item {
            SettingsSectionCard(
                title = if (isArabic) "المظهر والتصميم" else "Appearance",
                icon = Icons.Default.DarkMode
            ) {
                Column {
                    ThemeOptionRow(
                        title = if (isArabic) "الوضع الليلي (موصى به)" else "Dark Mode (Recommended)",
                        selected = themeMode == "dark",
                        onClick = { onThemeModeChange("dark") }
                    )
                    ThemeOptionRow(
                        title = if (isArabic) "الوضع النهاري" else "Light Mode",
                        selected = themeMode == "light",
                        onClick = { onThemeModeChange("light") }
                    )
                    ThemeOptionRow(
                        title = if (isArabic) "تلقائي حسب النظام" else "System Default",
                        selected = themeMode == "system",
                        onClick = { onThemeModeChange("system") }
                    )
                }
            }
        }

        // Language
        item {
            SettingsSectionCard(
                title = if (isArabic) "لغة التطبيق" else "Language",
                icon = Icons.Default.Language
            ) {
                Column {
                    ThemeOptionRow(
                        title = "العربية (Arabic - RTL)",
                        selected = currentLanguage == "ar",
                        onClick = { onLanguageChange("ar") }
                    )
                    ThemeOptionRow(
                        title = "English (LTR)",
                        selected = currentLanguage == "en",
                        onClick = { onLanguageChange("en") }
                    )
                }
            }
        }

        // Storage & Cache
        item {
            SettingsSectionCard(
                title = if (isArabic) "التخزين والشبكة" else "Storage & Network",
                icon = Icons.Default.CleaningServices
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "التنزيل عبر Wi-Fi فقط" else "Download via Wi-Fi only",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isArabic) "توفير باقة بيانات الهاتف عند تنزيل ملفات الخطوط." else "Save mobile data when downloading fonts.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isWifiOnly,
                            onCheckedChange = onWifiOnlyToggle,
                            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onClearCache() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "مسح الذاكرة المؤقتة للخطوط" else "Clear Cached Fonts",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = if (isArabic) "حذف الحزم المصدرة والملفات المؤقتة لتحرير المساحة." else "Delete temporary exported bundles to free up storage.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // About & Safety
        item {
            SettingsSectionCard(
                title = if (isArabic) "حول التطبيق والأمان" else "About & Safety",
                icon = Icons.Default.Info
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showAboutDialog = true }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "حول FontX الإصدار 1.0.0" else "About FontX v1.0.0",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showPrivacyDialog = true }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "سياسة الخصوصية وميثاق الأمان" else "Privacy & Security Policy",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(listOf(PrimaryViolet, SecondaryCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TextFields,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "FontX", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = if (isArabic) {
                        "تطبيق FontX هو منصة احترافية لإدارة وتخصيص خطوط هواتف أندرويد لجميع الشركات: Samsung, Xiaomi, Huawei, HONOR, OPPO, vivo, realme, OnePlus, TECNO, Infinix, والمزيد. صُمم ليعمل بنسبة 100% بدون روت وبأعلى معايير الأمان."
                    } else {
                        "FontX is a professional Android phone typography and font management platform supporting Samsung, Xiaomi, Huawei, HONOR, OPPO, vivo, realme, OnePlus, TECNO, Infinix, and more. 100% zero-root and secure."
                    },
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(text = if (isArabic) "حسنًا" else "OK")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isArabic) "سياسة الأمان والخصوصية" else "Privacy & Safety Policy",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = if (isArabic) {
                        "1. لا يقوم FontX بطلب صلاحيات الروت أو تعديل ملفات النظام الحساسة.\n" +
                                "2. لا نجمع أي بيانات شخصية أو سجلات استخدام خاصة.\n" +
                                "3. جميع عمليات فحص الخطوط والتحقق من التوقيع تتم محليًا داخل جهازك.\n" +
                                "4. يتم استخدام الطرق الرسمية المتاحة من قبل كل مصنع للهاتف."
                    } else {
                        "1. FontX never requires root or modifies sensitive system files.\n" +
                                "2. We collect zero personal data or private telemetry.\n" +
                                "3. All font validation and rendering occurs locally on your device.\n" +
                                "4. We strictly use official OEM customization settings and APIs."
                    },
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(text = if (isArabic) "حسنًا" else "OK")
                }
            }
        )
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderDark, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun ThemeOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
        )
    }
}
