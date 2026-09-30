package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun DeviceStatusCard(
    deviceInfo: DeviceInfo,
    isArabic: Boolean,
    onCheckCompatibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    val supportBadgeColor = when (deviceInfo.supportLevel) {
        FontSupportLevel.NATIVE_THEME_ENGINE -> StatusSuccess
        FontSupportLevel.SETTINGS_FONT_PICKER -> Color(0xFF38BDF8)
        FontSupportLevel.RESTRICTED_MANUAL_GUIDE -> StatusWarning
    }

    val supportBadgeText = when (deviceInfo.supportLevel) {
        FontSupportLevel.NATIVE_THEME_ENGINE -> if (isArabic) "مدعوم عبر مدير الثيمات" else "Theme Engine Supported"
        FontSupportLevel.SETTINGS_FONT_PICKER -> if (isArabic) "مدعوم عبر إعدادات الشاشة" else "Display Settings Supported"
        FontSupportLevel.RESTRICTED_MANUAL_GUIDE -> if (isArabic) "يتطلب تثبيت يدوي / تصدير" else "Manual / Export Required"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("device_status_card")
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(MaterialTheme.colorScheme.primary, SecondaryCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "Device Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${deviceInfo.brand} ${deviceInfo.model}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Android ${deviceInfo.androidVersion} • ${deviceInfo.romName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = supportBadgeColor.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (deviceInfo.supportLevel == FontSupportLevel.RESTRICTED_MANUAL_GUIDE) {
                            Icons.Default.Warning
                        } else {
                            Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = supportBadgeColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = supportBadgeText,
                        color = supportBadgeColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isArabic) deviceInfo.summaryAr else deviceInfo.summaryEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCheckCompatibility,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("check_compatibility_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isArabic) "اكتشاف طريقة التثبيت والفحص الشامل" else "Check Method & Compatibility",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
