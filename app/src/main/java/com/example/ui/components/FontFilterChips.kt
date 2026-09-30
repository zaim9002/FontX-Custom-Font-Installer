package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FontFilterChips(
    selectedCategory: String,
    isArabic: Boolean,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = if (isArabic) {
        listOf(
            "الكل" to "All",
            "عربي" to "Arabic",
            "إنجليزي" to "English",
            "عصري" to "Modern",
            "كلاسيكي" to "Classic",
            "رقعة ونسخ" to "Calligraphy",
            "أنيق" to "Elegant",
            "نمط iOS" to "iOS Style",
            "ألعاب" to "Gaming"
        )
    } else {
        listOf(
            "All" to "All",
            "Arabic" to "Arabic",
            "English" to "English",
            "Modern" to "Modern",
            "Classic" to "Classic",
            "Calligraphy" to "Calligraphy",
            "Elegant" to "Elegant",
            "iOS Style" to "iOS Style",
            "Gaming" to "Gaming"
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { (label, key) ->
            val isSelected = selectedCategory.equals(key, ignoreCase = true) ||
                    selectedCategory.equals(label, ignoreCase = true)
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(key) },
                label = {
                    Text(
                        text = label,
                        fontSize = 13.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
