package ir.atiran.hamrah.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.R
import ir.atiran.hamrah.ui.components.AppBarGlass
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * درباره برنامه
 */
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Space900)
    ) {
        AuroraBackground()

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AppBarGlass(title = "درباره برنامه", onBack = onBack)

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                Box(
                    Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0x3000C9B1), Color.Transparent))),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.app_logo),
                        contentDescription = "لوگوی آتیران همراه",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(120.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))
                Text(
                    "آتیران همراه",
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "همراه هوشمند فروش، ویزیت و وصول",
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(10.dp))
                StatusChip("نسخه ۱.۰.۰", Teal500)

                Spacer(Modifier.height(24.dp))

                AboutRow(Icons.Rounded.Smartphone, "نسخه اندروید بومی", "بازنویسی کامل اپلیکیشن ویندوزی با Kotlin و Jetpack Compose")
                Spacer(Modifier.height(10.dp))
                AboutRow(Icons.Rounded.Storage, "اتصال به سامانه موجود", "سازگار با سرویس WCF «AtiranLocalServices» و ساختار پایگاه داده sac")
                Spacer(Modifier.height(10.dp))
                AboutRow(Icons.Rounded.Build, "کار آفلاین", "ثبت فاکتور و ویزیت بدون اینترنت؛ ارسال خودکار هنگام اتصال (صف همگام‌سازی)")
                Spacer(Modifier.height(10.dp))
                AboutRow(Icons.Rounded.Security, "امنیت", "بدون ذخیره رمز عبور روی دستگاه؛ توصیه استقرار HTTPS در سرور")

                Spacer(Modifier.height(24.dp))

                Text(
                    "این برنامه برای تیم فروش سیار شرکت‌های توزیع ساخته شده است:\nثبت سفارش، مدیریت مشتریان، پیگیری چک، مسیر روزانه و گزارش‌های فروش — همه در یک همراه جیبی.",
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun AboutRow(icon: ImageVector, title: String, subtitle: String) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x1A00C9B1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = Teal500, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    subtitle,
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 11.sp
                )
            }
        }
    }
}
