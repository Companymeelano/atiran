package ir.atiran.hamrah.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle2
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.core.util.Jalali
import ir.atiran.hamrah.core.util.money
import ir.atiran.hamrah.core.util.toPersianDigits
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AtiranButton3D
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.AvatarCircle
import ir.atiran.hamrah.ui.components.Btn3DStyle
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.GlassIconButton
import ir.atiran.hamrah.ui.components.GradientBar
import ir.atiran.hamrah.ui.components.MenuGrid
import ir.atiran.hamrah.ui.components.NumberTicker
import ir.atiran.hamrah.ui.components.SectionHeader
import ir.atiran.hamrah.ui.components.StatOrb
import ir.atiran.hamrah.ui.components.TileData
import ir.atiran.hamrah.ui.theme.Danger
import ir.atiran.hamrah.ui.theme.Gold400
import ir.atiran.hamrah.ui.theme.Info
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Success
import ir.atiran.hamrah.ui.theme.Teal400
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn
import ir.atiran.hamrah.ui.theme.Warn

/**
 * داشبورد اصلی: آمار روز، اقدامات سریع و منوی کاشی‌های سه‌بعدی
 */
@Composable
fun HomeScreen(vm: MainViewModel, onOpen: (String) -> Unit) {
    val visitor by vm.visitor.collectAsState()
    val stats by vm.stats.collectAsState()
    val visits by vm.visits.collectAsState()
    val checks by vm.checks.collectAsState()
    val customers by vm.customers.collectAsState()

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
                .statusBarsPadding()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(10.dp))

            /* ---------- سرصفحه ---------- */
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(name = visitor?.name ?: "کاربر", sizeDp = 48)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "${greeting()}، ${visitor?.name ?: "کاربر"}",
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        Jalali.todayFull(),
                        color = TextSecondary,
                        fontFamily = Vazirmatn,
                        fontSize = 11.5.sp
                    )
                }
                GlassIconButton(
                    icon = Icons.Rounded.Notifications,
                    contentDescription = "پیام‌ها",
                    onClick = { onOpen("messages") }
                )
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- کارت قهرمان فروش امروز ---------- */
            GlassCard(Modifier.fillMaxWidth(), cornerRadius = 28) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 92.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.TrendingUp, null, tint = Teal400, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "فروش امروز شما",
                                color = TextSecondary,
                                fontFamily = Vazirmatn,
                                fontSize = 12.5.sp
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        NumberTicker(
                            value = stats.salesToday,
                            suffix = " تومان",
                            fontSize = 24,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(12.dp))
                        GradientBar(
                            progress = stats.progress,
                            brush = Brush.horizontalGradient(listOf(Teal400, Gold400))
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "هدف روزانه: ${stats.target.money()} (${(stats.progress * 100).toInt()}٪ محقق‌شده)".toPersianDigits(),
                            color = TextSecondary,
                            fontFamily = Vazirmatn,
                            fontSize = 11.5.sp
                        )
                    }
                    // گوی پیشرفت سمت چپ کارت
                    StatOrb(
                        progress = stats.progress,
                        color = Teal400,
                        sizeDp = 86.dp,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${(stats.progress * 100).toInt()}٪".toPersianDigits(),
                                color = TextPrimary,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                "از هدف",
                                color = TextSecondary,
                                fontFamily = Vazirmatn,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            /* ---------- آمار سریع ---------- */
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickStat(
                    Modifier.weight(1f),
                    icon = Icons.Rounded.ReceiptLong,
                    label = "فاکتور امروز",
                    value = stats.invoiceCount.toPersianDigits(),
                    tint = Teal400
                )
                QuickStat(
                    Modifier.weight(1f),
                    icon = Icons.Rounded.CheckCircle2,
                    label = "ویزیت انجام‌شده",
                    value = "${stats.visitsDone.toPersianDigits()} از ${stats.visitsTotal.toPersianDigits()}",
                    tint = Success
                )
                QuickStat(
                    Modifier.weight(1f),
                    icon = Icons.Rounded.Payments,
                    label = "وصولی امروز",
                    value = (stats.collectionToday / 1_000_000).toPersianDigits() + " م",
                    tint = Gold400
                )
            }

            Spacer(Modifier.height(18.dp))

            /* ---------- دکمه‌های سه‌بعدی اقدام سریع ---------- */
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    AtiranButton3D(
                        text = "فاکتور جدید",
                        icon = Icons.Rounded.ReceiptLong,
                        style = Btn3DStyle.GOLD,
                        height = 52.dp,
                        onClick = { onOpen("new_invoice") }
                    )
                }
                Box(Modifier.weight(1f)) {
                    AtiranButton3D(
                        text = "شروع ویزیت",
                        icon = Icons.Rounded.LocationOn,
                        style = Btn3DStyle.PRIMARY,
                        height = 52.dp,
                        onClick = { onOpen("visits") }
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            /* ---------- منوی کاشی‌های سه‌بعدی ---------- */
            SectionHeader(title = "دسترسی سریع", actionLabel = "منوی کامل", onAction = { onOpen("more") })
            Spacer(Modifier.height(6.dp))

            MenuGrid(
                tiles = listOf(
                    TileData(
                        route = "customers",
                        title = "مشتریان",
                        subtitle = "${customers.size} مشتری فعال",
                        icon = Icons.Rounded.Groups,
                        gradient = listOf(Color(0xFF00564C), Color(0xFF003832)),
                        glow = Color(0xFF00C9B1)
                    ),
                    TileData(
                        route = "invoices",
                        title = "فاکتورها",
                        subtitle = "فروش و پیش‌فاکتور",
                        icon = Icons.Rounded.ReceiptLong,
                        gradient = listOf(Color(0xFF123A8A), Color(0xFF0A2255)),
                        glow = Color(0xFF5B8DEF)
                    ),
                    TileData(
                        route = "products",
                        title = "کالا و انبار",
                        subtitle = "قیمت و موجودی",
                        icon = Icons.Rounded.Inventory2,
                        gradient = listOf(Color(0xFF7A4E00), Color(0xFF4A2E00)),
                        glow = Color(0xFFFFC94A)
                    ),
                    TileData(
                        route = "checks",
                        title = "چک‌ها",
                        subtitle = "${checks.count { it.status == ir.atiran.hamrah.data.model.CheckStatus.IN_POCKET }} چک در جیب",
                        icon = Icons.Rounded.Payments,
                        gradient = listOf(Color(0xFF5C1F6B), Color(0xFF371242)),
                        glow = Color(0xFFB57BFF)
                    ),
                    TileData(
                        route = "visits",
                        title = "مسیر و ویزیت",
                        subtitle = "${visits.count { it.status == ir.atiran.hamrah.data.model.VisitStatus.PENDING }} توقف باقی‌مانده",
                        icon = Icons.Rounded.Route,
                        gradient = listOf(Color(0xFF0E5D4E), Color(0xFF083A31)),
                        glow = Color(0xFF37D6B5)
                    ),
                    TileData(
                        route = "reports",
                        title = "گزارش‌ها",
                        subtitle = "تحلیل فروش",
                        icon = Icons.Rounded.Insights,
                        gradient = listOf(Color(0xFF8A2D5E), Color(0xFF521537)),
                        glow = Color(0xFFFF7EB3)
                    )
                ),
                onTile = onOpen
            )

            Spacer(Modifier.height(22.dp))

            /* ---------- هشدار چک‌های نزدیک سررسید ---------- */
            val dueSoon = checks.filter {
                it.status == ir.atiran.hamrah.data.model.CheckStatus.IN_POCKET ||
                        it.status == ir.atiran.hamrah.data.model.CheckStatus.DEPOSITED
            }
            if (dueSoon.isNotEmpty()) {
                GlassCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.WarningAmber, null, tint = Warn, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "چک‌های در جریان وصول",
                                color = TextPrimary,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        dueSoon.take(3).forEach { check ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier
                                        .size(8.dp)
                                        .background(Danger.copy(alpha = 0.8f), androidx.compose.foundation.shape.CircleShape)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    check.customerName,
                                    color = TextPrimary,
                                    fontFamily = Vazirmatn,
                                    fontSize = 12.5.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "${check.amount.money()} • ${check.dueDate}",
                                    color = TextSecondary,
                                    fontFamily = Vazirmatn,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "مشاهده همه چک‌ها",
                            color = Teal400,
                            fontFamily = Vazirmatn,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                                .clickable { onOpen("checks") }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

private fun greeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "صبح بخیر"
        hour < 17 -> "ظهر بخیر"
        hour < 20 -> "عصر بخیر"
        else -> "شب بخیر"
    }
}

@Composable
private fun QuickStat(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    GlassCard(modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                color = TextPrimary,
                fontFamily = Vazirmatn,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                label,
                color = TextSecondary,
                fontFamily = Vazirmatn,
                fontSize = 10.sp
            )
        }
    }
}
