package com.example.proyectoruben.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.proyectoruben.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

private val montserrat = androidx.compose.ui.text.font.FontFamily(
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = provider,
        weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = provider,
        weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = provider,
        weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = provider,
        weight = FontWeight.Bold),
)

val AppTypography = Typography(
    // Títulos grandes — bold, estilo cabecera del escritorio
    displayLarge = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.Bold,
        fontSize = 57.sp, lineHeight = 64.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 30.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 26.sp
    ),
    // Subtítulos
    titleLarge = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp
    ),
    titleMedium = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.Medium,
        fontSize = 13.sp, lineHeight = 20.sp
    ),
    // Cuerpo
    bodyLarge = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp
    ),
    // Etiquetas
    labelLarge = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = montserrat, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp
    ),
)