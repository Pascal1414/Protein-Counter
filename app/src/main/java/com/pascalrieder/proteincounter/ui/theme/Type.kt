package com.pascalrieder.proteincounter.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.pascalrieder.proteincounter.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily(
            Font(googleFont = GoogleFont("Eczar"), fontProvider = provider)
        ),
        fontSize = 48.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily(
            Font(googleFont = GoogleFont("Eczar"), fontProvider = provider)
        ),
        fontSize = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily(
            Font(googleFont = GoogleFont("Eczar"), fontProvider = provider)
        ),
        fontSize = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily(
            Font(googleFont = GoogleFont("Eczar"), fontProvider = provider)
        ),
        fontSize = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily(
            Font(googleFont = GoogleFont("Eczar"), fontProvider = provider)
        ),
        fontSize = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily(
            Font(googleFont = GoogleFont("Eczar"), fontProvider = provider)
        ),
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily(
            Font(googleFont = GoogleFont("Roboto Condensed"), fontProvider = provider)
        ),
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(
            Font(googleFont = GoogleFont("Roboto Condensed"), fontProvider = provider)
        ),
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily(
            Font(googleFont = GoogleFont("Roboto Condensed"), fontProvider = provider)
        ),
        fontSize = 12.sp
    ),
)