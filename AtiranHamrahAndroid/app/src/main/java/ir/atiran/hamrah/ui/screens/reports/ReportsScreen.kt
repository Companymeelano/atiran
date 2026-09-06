package ir.atiran.hamrah.ui.screens.reports

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.core.util.money
import ir.atiran.hamrah.core.util.toPersianDigits
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.SectionHeader
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.theme.Gold400
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Success
import ir.atiran.hamrah.ui.theme.Teal400
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * گزارش‌ها: نمودار فروش هفتگی (کشیده‌شده با Canvas بدون کتابخانه)،
 * برترین مشتری و صف همگام‌سازی
 */
@Composable
fun ReportsScreen(vm: MainViewModel) {
    val weekly by vm.weekly.collectAsState()
    val invoices by vm.invoices.collectAsState()
    val customers by vm.customers.collectAsState()
    val pending by vm.pending.collectAsState()
    val stats by vm.stats.collectAsState()

    // برترین مشتری بر اساس مجموع خرید
    val best = invoices
        .filter { !it.isPre }
        .groupBy { it.customerShmo }
        .map { (shmo, list) -> shmo to list.sumOf { it.total } }
        .maxByOrNull { it.second }
    val bestCustomer = customers.firstOrNull { it.shmo == best?.first }

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
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "گزارش‌ها",
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )
                Spacer(Modifier.weight(1f))
                StatusChip("۷ روز اخیر", Teal500)
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- نمودار ستونی فروش هفتگی ---------- */
            GlassCard(Modifier.fillMaxWidth(), cornerRadius = 26) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Insights, null, tint = Teal400, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "روند فروش هفتگی",
                            color = TextPrimary,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    WeeklyChart(values = weekly.map { it.value }, labels = weekly.map { it.dayName })
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- کارت‌های عملکرد ---------- */
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassCard(Modifier.weight(1f)) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.TrendingUp, null, tint = Teal400, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.height(6.dp))
                        Text(
                            stats.avgBasket.money(),
                            color = TextPrimary,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "میانگین سبد خرید",
                            color = TextSecondary,
                            fontFamily = Vazirmatn,
                            fontSize = 10.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                GlassCard(Modifier.weight(1f)) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.EmojiEvents, null, tint = Gold400, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "${invoices.size.toPersianDigits()} سند",
                            color = TextPrimary,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "مجموع اسناد فروش",
                            color = TextSecondary,
                            fontFamily = Vazirmatn,
                            fontSize = 10.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- برترین مشتری ---------- */
            if (bestCustomer != null && best != null) {
                GlassCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFFF5A623), Color(0xFFB97E00)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.EmojiEvents, null, tint = Color(0xFF3D2A00), modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "مشتری برتر هفته",
                                color = TextSecondary,
                                fontFamily = Vazirmatn,
                                fontSize = 11.sp
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                bestCustomer.name,
                                color = TextPrimary,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            best.second.money(),
                            color = Gold400,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- صف همگام‌سازی ---------- */
            SectionHeader(title = "صف همگام‌سازی با سرور")
            Spacer(Modifier.height(8.dp))
            if (pending.isEmpty()) {
                GlassCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.CloudDone, null, tint = Success, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "همه عملیات‌ها با سرور همگام شده‌اند",
                            color = TextSecondary,
                            fontFamily = Vazirmatn,
                            fontSize = 12.5.sp
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pending.forEach { op ->
                        GlassCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Sync, null, tint = Teal400, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        op.title,
                                        color = TextPrimary,
                                        fontFamily = Vazirmatn,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        "${op.kind} • ${op.createdAt}",
                                        color = TextSecondary,
                                        fontFamily = Vazirmatn,
                                        fontSize = 10.sp
                                    )
                                }
                                StatusChip("در صف", Gold400)
                            }
                        }
                    }
                    // دکمه همگام‌سازی
                    Text(
                        "تلاش برای همگام‌سازی",
                        color = Color(0xFF0A0F1E),
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Teal500)
                            .clickable { vm.flushQueue() }
                            .padding(vertical = 13.dp)
                    )
                }
            }

            Spacer(Modifier.height(130.dp))
        }
    }
}

/**
 * نمودار ستونی هفتگی با انیمیشن رشد ستون‌ها — تماماً با Canvas
 */
@Composable
private fun WeeklyChart(values: List<Double>, labels: List<String>) {
    var started by remember { mutableStateOf(false) }
    val progress by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 900,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "chart"
    )
    androidx.compose.runtime.LaunchedEffect(Unit) { started = true }

    val maxVal = (values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)

    Column(Modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val barCount = values.size
            if (barCount == 0) return@Canvas
            val slot = size.width / barCount
            val barWidth = slot * 0.52f
            val chartHeight = size.height - 8.dp.toPx()

            values.forEachIndexed { i, v ->
                val ratio = (v / maxVal).toFloat() * progress
                val h = chartHeight * ratio
                val x0 = i * slot + (slot - barWidth) / 2f
                val top = chartHeight - h
                // ستون با گرادیانت
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Teal400.copy(alpha = 0.95f), Teal400.copy(alpha = 0.35f)),
                        startY = top,
                        endY = top + h
                    ),
                    topLeft = Offset(x0, top),
                    size = Size(barWidth, h),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
                // درخشش نوک ستون
                if (ratio > 0.04f) {
                    drawRoundRect(
                        color = Color(0xFF6FF0DC),
                        topLeft = Offset(x0, top),
                        size = Size(barWidth, 3.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx())
                    )
                }
            }
            // خط زمینه
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0x10FFFFFF), Color.Transparent)
                ),
                topLeft = Offset(0f, chartHeight),
                size = Size(size.width, 1.dp.toPx())
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Text(
                    label,
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 9.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
