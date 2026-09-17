package com.bk.mmovies.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
        bodyLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
                // Default Trim.Both shaves padding tight to the 24sp line
                // height, which clips the bottom of bold-weight glyphs (e.g.
                // the selected CatalogNavRailItem label). Trim.None keeps
                // the font's natural top/bottom padding instead.
                lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                                                  )
                             )
                           )