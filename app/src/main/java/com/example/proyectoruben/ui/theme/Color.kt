package com.example.proyectoruben.ui.theme

import androidx.compose.ui.graphics.Color

// ── Paleta principal inspirada en la app de escritorio ──────────────────────
// Primario: Gris carbón oscuro  (#2D3436)
// Acento:   Dorado cálido       (#D4A574 / #C9A86C)
// Fondo:    Casi negro / blanco roto

// Light scheme
val PrimaryLight          = Color(0xFF2D3436)   // Gris carbón
val OnPrimaryLight        = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFF4A5568)
val OnPrimaryContainerLight = Color(0xFFF5F6F7)

val SecondaryLight          = Color(0xFFD4A574)  // Dorado cálido (AccentColor del WPF)
val OnSecondaryLight        = Color(0xFF2D3436)
val SecondaryContainerLight = Color(0xFFF5E6D3)
val OnSecondaryContainerLight = Color(0xFF4A3520)

val TertiaryLight          = Color(0xFFC9A86C)   // GoldAccent del WPF
val OnTertiaryLight        = Color(0xFF2D3436)
val TertiaryContainerLight = Color(0xFFF0DFC0)
val OnTertiaryContainerLight = Color(0xFF3D2E10)

val BackgroundLight        = Color(0xFFF5F6F7)   // LightGray del WPF
val OnBackgroundLight      = Color(0xFF2D3436)
val SurfaceLight           = Color(0xFFFFFFFF)
val OnSurfaceLight         = Color(0xFF2D3436)
val SurfaceVariantLight    = Color(0xFFDFE6E9)   // MediumGray del WPF
val OnSurfaceVariantLight  = Color(0xFF636E72)
val OutlineLight           = Color(0xFF636E72)
val OutlineVariantLight    = Color(0xFFDFE6E9)

val ErrorLight             = Color(0xFFD63031)   // ErrorBrush del WPF
val OnErrorLight           = Color(0xFFFFFFFF)
val ErrorContainerLight    = Color(0xFFFFDAD6)
val OnErrorContainerLight  = Color(0xFF93000A)

// Dark scheme
val PrimaryDark            = Color(0xFFD4A574)   // Dorado como primario en oscuro
val OnPrimaryDark          = Color(0xFF2D3436)
val PrimaryContainerDark   = Color(0xFF4A3520)
val OnPrimaryContainerDark = Color(0xFFF5E6D3)

val SecondaryDark          = Color(0xFFC9A86C)
val OnSecondaryDark        = Color(0xFF2D3436)
val SecondaryContainerDark = Color(0xFF3D2E10)
val OnSecondaryContainerDark = Color(0xFFF0DFC0)

val TertiaryDark           = Color(0xFFB8956A)
val OnTertiaryDark         = Color(0xFF1A1A1A)
val TertiaryContainerDark  = Color(0xFF3D2E10)
val OnTertiaryContainerDark = Color(0xFFF0DFC0)

val BackgroundDark         = Color(0xFF1A1D1E)   // Más oscuro que el WPF para móvil
val OnBackgroundDark       = Color(0xFFF5F6F7)
val SurfaceDark            = Color(0xFF2D3436)   // PrimaryColor del WPF como superficie
val OnSurfaceDark          = Color(0xFFF5F6F7)
val SurfaceVariantDark     = Color(0xFF3D4448)
val OnSurfaceVariantDark   = Color(0xFFB0B8BB)
val OutlineDark            = Color(0xFF636E72)
val OutlineVariantDark     = Color(0xFF3D4448)

val ErrorDark              = Color(0xFFFF6B6B)
val OnErrorDark            = Color(0xFF690005)
val ErrorContainerDark     = Color(0xFF93000A)
val OnErrorContainerDark   = Color(0xFFFFDAD6)