package ir.atiran.hamrah.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/* ---------- طرح رنگ تیره (پیش‌فرض و اصلی برنامه) ---------- */
private val DarkScheme = darkColorScheme(
    primary = Teal500,
    onPrimary = Color(0xFF00332D),
    primaryContainer = Teal800,
    onPrimaryContainer = Teal300,
    inversePrimary = Teal700,
    secondary = Gold500,
    onSecondary = Color(0xFF3D2A00),
    secondaryContainer = Color(0xFF4A3400),
    onSecondaryContainer = Gold300,
    tertiary = Violet,
    onTertiary = Color(0xFF1D1747),
    background = Space900,
    onBackground = TextPrimary,
    surface = Space900,
    onSurface = TextPrimary,
    surfaceVariant = Space800,
    onSurfaceVariant = TextSecondary,
    surfaceTint = Teal500,
    inverseSurface = Color(0xFFE6EBF8),
    inverseOnSurface = Space900,
    error = Danger,
    onError = Color(0xFF5C0E0E),
    errorContainer = Color(0xFF5C1B1B),
    onErrorContainer = Color(0xFFFFD2D2),
    outline = Color(0xFF39466B),
    outlineVariant = Color(0xFF232E4D),
    scrim = Space950
)

/* ---------- طرح رنگ روشن ---------- */
private val LightScheme = lightColorScheme(
    primary = Teal700,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB8F2E7),
    onPrimaryContainer = Color(0xFF00201B),
    secondary = Gold700,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFE9B8),
    onSecondaryContainer = Color(0xFF3D2A00),
    tertiary = Color(0xFF6C5CE0),
    background = LightBg,
    onBackground = TextOnLight,
    surface = LightSurface,
    onSurface = TextOnLight,
    surfaceVariant = LightSurfaceAlt,
    onSurfaceVariant = TextOnLightSecondary,
    error = Color(0xFFC0392B),
    outline = Color(0xFF8A93B2),
    outlineVariant = Color(0xFFD4DCEC)
)

/** شکل‌های گرد و نرم مشخصه رابط کاربری آتیران */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(34.dp)
)

@Composable
fun HamrahTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
