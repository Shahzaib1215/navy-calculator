package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val NavyDarkColorScheme =
    darkColorScheme(
        primary = Navy500,
        onPrimary = PureWhite,
        primaryContainer = Navy800,
        onPrimaryContainer = IceNavy100,
        secondary = NavyAccent,
        onSecondary = PureWhite,
        secondaryContainer = Navy700,
        onSecondaryContainer = PureWhite,
        tertiary = IceNavy200,
        onTertiary = Navy950,
        background = Navy950,
        onBackground = PureWhite,
        surface = Navy900,
        onSurface = PureWhite,
        surfaceVariant = Navy800,
        onSurfaceVariant = IceNavy200,
        outline = SlateBorderDark,
        outlineVariant = Navy800,
        error = ErrorRed,
        onError = PureWhite
    )

private val NavyLightColorScheme =
    lightColorScheme(
        primary = Navy700,
        onPrimary = PureWhite,
        primaryContainer = IceNavy100,
        onPrimaryContainer = Navy900,
        secondary = Navy500,
        onSecondary = PureWhite,
        secondaryContainer = IceNavy50,
        onSecondaryContainer = Navy700,
        tertiary = NavyAccent,
        onTertiary = PureWhite,
        background = PureWhite,
        onBackground = NavyTextPrimary,
        surface = PureWhite,
        onSurface = NavyTextPrimary,
        surfaceVariant = IceNavy50,
        onSurfaceVariant = NavyTextSecondary,
        outline = IceNavy200,
        outlineVariant = SlateBorderLight,
        error = ErrorRed,
        onError = PureWhite
    )

@Composable
fun NavyCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve White & Navy theme explicitly
    content: @Composable () -> Unit
) {
  val colorScheme =
      when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> NavyDarkColorScheme
        else -> NavyLightColorScheme
      }

  MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
  )
}
