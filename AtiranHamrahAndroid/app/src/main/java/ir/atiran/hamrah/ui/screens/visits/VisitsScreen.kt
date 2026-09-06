package ir.atiran.hamrah.ui.screens.visits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle2
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SkipNext
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
import ir.atiran.hamrah.core.util.toPersianDigits
import ir.atiran.hamrah.data.model.VisitStatus
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AppBarGlass
import ir.atiran.hamrah.ui.components.AtiranButton3D
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.Btn3DStyle
import ir.atiran.hamrah.ui.components.EmptyState
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.StatOrb
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.theme.Danger
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Success
import ir.atiran.hamrah.ui.theme.Teal400
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn
import ir.atiran.hamrah.ui.theme.Warn

/**
 * مسیر و ویزیت روز: تایم‌لاین توقف‌ها با چک‌این یک‌لمسی
 */
@Composable
fun VisitsScreen(vm: MainViewModel, onOpenCustomer: (Int) -> Unit) {
    val visits by vm.visits.collectAsState()
    val done = visits.count { it.status == VisitStatus.DONE }
    val progress = if (visits.isEmpty()) 0f else done.toFloat() / visits.size

    Box(
        Modifier
            .fillMaxSize()
            .background(Space900)
    ) {
        AuroraBackground()

        Column(
            Modifier
                .fillMaxSize()
        ) {
            AppBarGlass(title = "مسیر و ویزیت امروز")

            Column(Modifier.padding(horizontal = 18.dp)) {
                /* ---------- کارت پیشرفت مسیر ---------- */
                GlassCard(Modifier.fillMaxWidth(), cornerRadius = 26) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Route, null, tint = Teal400, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "پیشرفت مسیر امروز",
                                    color = TextSecondary,
                                    fontFamily = Vazirmatn,
                                    fontSize = 12.5.sp
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${done.toPersianDigits()} از ${visits.size.toPersianDigits()} توقف انجام‌شده",
                                color = TextPrimary,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                if (visits.any { it.status == VisitStatus.PENDING })
                                    "ایستگاه بعدی: " + visits.first { it.status == VisitStatus.PENDING }.customerName
                                else if (done > 0) "مسیر امروز کامل شد! 🎉"
                                else "مسیری برای امروز ثبت نشده",
                                color = TextSecondary,
                                fontFamily = Vazirmatn,
                                fontSize = 11.5.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        StatOrb(progress = progress, color = Teal400, sizeDp = 92.dp) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${(progress * 100).toInt()}٪".toPersianDigits(),
                                    color = TextPrimary,
                                    fontFamily = Vazirmatn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
            }

            /* ---------- تایم‌لاین توقف‌ها ---------- */
            if (visits.isEmpty()) {
                EmptyState(
                    icon = Icons.Rounded.Route,
                    title = "مسیر امروز خالی است",
                    subtitle = "از بخش هماهنگی توزیع مسیر جدید ثبت شود"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 18.dp, end = 18.dp, bottom = 130.dp
                    )
                ) {
                    itemsIndexed(visits, key = { _, v -> v.visitId }) { index, stop ->
                        VisitTimelineItem(
                            stop = stop,
                            isLast = index == visits.lastIndex,
                            onOpenCustomer = { onOpenCustomer(stop.shmo) },
                            onCheckIn = { vm.checkIn(stop.visitId) },
                            onSkip = { vm.skipVisit(stop.visitId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VisitTimelineItem(
    stop: ir.atiran.hamrah.data.model.VisitStop,
    isLast: Boolean,
    onOpenCustomer: () -> Unit,
    onCheckIn: () -> Unit,
    onSkip: () -> Unit
) {
    Row(Modifier.fillMaxWidth()) {
        /* --- ستون تایم‌لاین --- */
        Column(
            Modifier.width(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        when (stop.status) {
                            VisitStatus.DONE -> Success
                            VisitStatus.SKIPPED -> Color(0x55FFFFFF)
                            VisitStatus.PENDING -> Teal500
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (stop.status == VisitStatus.DONE) {
                    Icon(
                        Icons.Rounded.CheckCircle2,
                        contentDescription = null,
                        tint = Color(0xFF062A22),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .height(90.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x4D00C9B1), Color(0x1400C9B1))
                            )
                        )
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        /* --- کارت توقف --- */
        GlassCard(Modifier.weight(1f)) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${stop.order.toPersianDigits()}. ${stop.customerName}",
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        modifier = Modifier.weight(1f)
                    )
                    StatusChip(
                        when (stop.status) {
                            VisitStatus.DONE -> "انجام‌شده"
                            VisitStatus.PENDING -> "در انتظار"
                            VisitStatus.SKIPPED -> "حذف‌شده"
                        },
                        when (stop.status) {
                            VisitStatus.DONE -> Success
                            VisitStatus.PENDING -> Teal500
                            VisitStatus.SKIPPED -> Warn
                        }
                    )
                }
                if (!stop.address.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.LocationOn, null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            stop.address!!,
                            color = TextSecondary,
                            fontFamily = Vazirmatn,
                            fontSize = 10.5.sp
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Schedule, null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stop.window,
                        color = TextSecondary,
                        fontFamily = Vazirmatn,
                        fontSize = 11.sp
                    )
                    Spacer(Modifier.weight(1f))
                    if (stop.status == VisitStatus.PENDING) {
                        Icon(
                            Icons.Rounded.SkipNext,
                            contentDescription = "حذف از مسیر",
                            tint = Danger,
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onSkip)
                                .padding(3.dp)
                        )
                    }
                }
                if (stop.note != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "یادداشت: ${stop.note}",
                        color = TextSecondary,
                        fontFamily = Vazirmatn,
                        fontSize = 10.sp
                    )
                }
                if (stop.status == VisitStatus.PENDING) {
                    Spacer(Modifier.height(10.dp))
                    AtiranButton3D(
                        text = "ثبت ویزیت (چک‌این)",
                        icon = Icons.Rounded.LocationOn,
                        style = Btn3DStyle.PRIMARY,
                        height = 44.dp,
                        onClick = {
                            onCheckIn()
                            onOpenCustomer()
                        }
                    )
                }
            }
        }
    }
}
