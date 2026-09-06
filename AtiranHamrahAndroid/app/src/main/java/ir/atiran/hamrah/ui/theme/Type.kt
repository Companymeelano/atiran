package ir.atiran.hamrah.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import ir.atiran.hamrah.R

/** خانواده فونت فارسی «وزیرمتن» — همراه پروژه بسته‌بندی شده است (مجوز OFL) */
val Vazirmatn = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold)
)

private val base = Typography()

/** تایپوگرافی یکپارچه با وزیرمتن */
val AppTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Bold),
    displayMedium = base.displayMedium.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Bold),
    displaySmall = base.displaySmall.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Bold),
    headlineLarge = base.headlineLarge.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Bold),
    headlineMedium = base.headlineMedium.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Bold),
    headlineSmall = base.headlineSmall.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.SemiBold),
    titleSmall = base.titleSmall.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Medium),
    bodyLarge = base.bodyLarge.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Normal),
    bodyMedium = base.bodyMedium.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Normal),
    bodySmall = base.bodySmall.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Normal),
    labelLarge = base.labelLarge.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Medium),
    labelMedium = base.labelMedium.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Medium),
    labelSmall = base.labelSmall.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Medium)
)
