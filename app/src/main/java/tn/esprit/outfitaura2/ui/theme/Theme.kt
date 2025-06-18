// tn.esprit.outfitaura2.ui.theme/Theme.kt
package tn.esprit.outfitaura2.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Color definitions
private val Purple = Color(0xFFD0BCFF) // Light variant for dark mode
private val PurpleGrey = Color(0xFFCCC2DC) // Light variant
private val Pink = Color(0xFFEFB8C8) // Light variant

private val Purpledark = Color(0xFF6650a4) // Dark variant for light mode
private val PurpleGreydark = Color(0xFF625b71) // Dark variant
private val Pinkdark = Color(0xFF7D5260) // Dark variant

// Color schemes
private val DarkColorScheme = darkColorScheme(
    primary = Purple,
    secondary = PurpleGrey,
    tertiary = Pink
)

private val LightColorScheme = lightColorScheme(
    primary = Purpledark,
    secondary = PurpleGreydark,
    tertiary = Pinkdark
    /* Other default colors to override */
)

val OutfitTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun OutfitAura2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OutfitTypography,
        content = content
    )
}